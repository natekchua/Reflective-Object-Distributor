import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public class Deserializer {
    private HashMap<Integer, Object> map = new HashMap<>();

    /*

     */
    public Object deserialize(Document doc) {
        Element root = doc.getRootElement();
        List<Element> objects = root.getChildren();

        // Recreate Object Instances
        for(Element object : objects){
            int id = Integer.parseInt(object.getAttributeValue("id"));
            String cName = object.getAttributeValue("class");
            instantiateObject(object, id, cName);
        }

        // Set values of fields
        for(Element object : objects){
            int id = Integer.parseInt(object.getAttributeValue("id"));
            Object objInstance = map.get(id);
            Class c = objInstance.getClass();
            List<Element> fields = object.getChildren();
            setFieldValues(objInstance, c, fields);
        }
        return map.get(0);
    }

    /*

     */
    private void setFieldValues(Object objInstance, Class c, List<Element> fields) {
        try{
            if(!c.isArray()){
                for(Element field : fields){
                    String declaringClassName = field.getAttributeValue("declaringClass");
                    Class declaringClass = Class.forName(declaringClassName);
                    Attribute fName = field.getAttribute("name");
                    Field f = c.getDeclaredField(fName.getName());
                    f.setAccessible(true);

                    Element value = field.getChildren().get(0);
                    Object fValue = getFieldValue(value, declaringClass);
                    f.set(objInstance, fValue);
                }
            }else{
                Class compType = objInstance.getClass().getComponentType();
                for(Element field : fields){
                    String fContent = field.getText();
                    if(field.getText().equals("null"))
                        Array.set(objInstance, field.indexOf(field), null);
                    else if(field.getName().equals("value"))
                        Array.set(objInstance, field.indexOf(field), field);
                    else if(field.getName().equals("reference")){
                        Object obj = map.get(fContent);
                        Array.set(objInstance, field.indexOf(field), obj);
                    }
                }
            }
        }
        catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /*

     */
    private Object getFieldValue(Element valueTag, Class fClass) {
        String type = valueTag.getName();
        String value = valueTag.getValue();
        Object fValue = null;

        if(type.equals("value")) {
            if (fClass.equals(int.class))
                fValue = Integer.parseInt(value);
            else if (fClass.equals(double.class))
                fValue = Double.parseDouble(value);
            else if (fClass.equals(float.class))
                fValue = Float.parseFloat(value);
        }
        else if(type.equals("reference")) {
            int refID = Integer.parseInt(valueTag.getValue());
            return map.get(refID);
        }
        return fValue;
    }


    /*

     */
    private void instantiateObject(Element object, int id, String cName) {
        Object objInstance = null;
        try{
            Class c = Class.forName(cName);
            if(!c.isArray()){
                Constructor constructor = c.getDeclaredConstructor(null);
                constructor.setAccessible(true);
                objInstance = constructor.newInstance(null);
            }else{
                int length = Integer.parseInt(object.getAttributeValue("length"));
                Class compType = c.getComponentType();
                objInstance = Array.newInstance(compType, length);
            }
            map.put(id, objInstance);
        }catch(ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
    }

}
