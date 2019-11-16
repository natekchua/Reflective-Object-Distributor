import org.jdom2.Document;
import org.jdom2.Element;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;

public class Deserializer {

    private HashMap<Integer, Object> map = new HashMap<>();

    public Object deserialize(Document document) {
        Element root = document.getRootElement();
        List<Element> objects = root.getChildren();
        instantiateObjects(objects);
        setFieldValues(objects);
        return map.get(0);
    }

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