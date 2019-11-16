import org.jdom2.Document;
import org.jdom2.Element;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;

public class Deserializer {

    private HashMap<Integer, Object> map = new HashMap<>();

    /*
     deconstructs passed in XML document to recreate the objects from the XML doc.
     @param Document doc
     @Object root object from the map
     */
    public Object deserialize(Document doc) {
        Element root = doc.getRootElement();
        List<Element> objects = root.getChildren();
        instantiateObjects(objects);
        setFieldValues(objects);
        return map.get(0);
    }

    /*
        creates new instances of the object elements from the xml document
        @param List<Element> objects
     */
    private void instantiateObjects(List<Element> objects) {
        Object objInstance;
        try {
            for (Element object : objects) {
                String className = object.getAttribute("class").getValue();
                Class c = Class.forName(className);

                if (!c.isArray()) {
                    Constructor constructor = c.getDeclaredConstructor(null);
                    constructor.setAccessible(true);
                    objInstance = constructor.newInstance(null);
                } else {
                    int length = Integer.parseInt(object.getAttribute("length").getValue());
                    Class compType = c.getComponentType();
                    objInstance = Array.newInstance(compType, length);
                }
                Integer id = Integer.parseInt(object.getAttribute("id").getValue());
                map.put(id, objInstance);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /*
     sets the field values of each new object instance
     @param List<ELement> fields
     */
    private void setFieldValues(List<Element> fields) {
        for (Element field : fields) {
            Object objInstance = map.get(Integer.parseInt(field.getAttributeValue("id")));
            List<Element> fieldTags = field.getChildren();
            Class c = objInstance.getClass();

            if (!c.isArray())
                setFields(objInstance, fieldTags);
            else
                setArray(objInstance, fieldTags);
        }
    }

    /*
     helper method for setFieldValues(). deals with all non-array field types.
     @param Object objInstance, List<Element> fields
     */
    private void setFields(Object objInstance, List<Element> fields) {
        try{
            for (Element field : fields) {
                String className = field.getAttributeValue("declaringClass");
                Class c = Class.forName(className);

                Field fieldToSet = c.getDeclaredField(field.getAttribute("name").getValue());
                int modifier = fieldToSet.getModifiers();
                if (Modifier.isFinal(modifier))
                    continue;

                fieldToSet.setAccessible(true);
                Element value = field.getChildren().get(0);

                if (value.getName().equals("value")) {
                    Class type = fieldToSet.getType();
                    fieldToSet.set(objInstance, getFieldValue(type, value));
                } else if (value.getName().equals("reference")) {
                    Object obj = map.get(Integer.parseInt(value.getText()));
                    fieldToSet.set(objInstance, obj);
                }
            }
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /*
    helper method for setFieldValues(). specific to arrays.
     */
    private void setArray(Object objInstance, List<Element> fields) {
        Class compType = objInstance.getClass().getComponentType();
        for (int i = 0; i < fields.size(); i++) {
            Element field = fields.get(i);
            String fContent = field.getText();
            String fName = field.getName();

            if (fContent.equals("null")) {
                Array.set(objInstance, i, null);
            } else if (fName.equals("value")) {
                Array.set(objInstance, i, getFieldValue(compType, field));
            } else if (fName.equals("reference")) {
                Object obj = map.get(Integer.parseInt(fContent));
                Array.set(objInstance, i, obj);
            }
        }
    }

    /*
       returns the field content depending on the class type.
       @param Class type, Element object
       @Object oContent
     */
    private Object getFieldValue(Class type, Element object) {
        String oContent = object.getText();
        if (type.equals(int.class))
            return Integer.valueOf(oContent);
        else if (type.equals(double.class))
            return Double.valueOf(oContent);
        else if (type.equals(float.class))
            return Float.valueOf(oContent);
        else
            return oContent;
    }
}