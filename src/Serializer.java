import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.w3c.dom.Attr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;

public class Serializer implements Serializable {

    private Document doc;
    private IdentityHashMap<Object, Integer> map = new IdentityHashMap<>();
    private int id = 0;

    public Document serialize(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        doc = new Document();
        doc.setRootElement(new Element("serialized"));

        Class c = obj.getClass();
        Method m = c.getDeclaredMethod("size", (Class<?>) null);
        Integer size = (Integer) m.invoke(obj, (Object) null);
        for(int i = 0; i < size; i++){
            Class<List> arrList = List.class;
            Method get = arrList.getDeclaredMethod("get", int.class);
            Object element = get.invoke(obj, i);
            serializeObject(element);
        }
        return doc;    //returns JDOM document
    }

    private void serializeObject(Object obj) throws IllegalAccessException {
        Class c = obj.getClass();
        Field[] fields = c.getDeclaredFields();

        // Object Tag
        Element objectTag = createObjectTag(obj);

        // Field Tag
        for(Field field : fields){
            field.setAccessible(true);
            Element fieldTag = createFieldTag(field);

            Object value = field.get(obj);
            Class type = field.getType();
            if(type.isPrimitive()){
                Element valueTag = createValueTag(obj);
                fieldTag.addContent(valueTag);
            }else{
                Element referenceTag = createReferenceTag(value);
                fieldTag.addContent(referenceTag);
                serializeObject(value);
            }
        }
    }

    private Element serializeArray(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class c = obj.getClass();

        // Object Tag
//        Element objectTag = new Element("object");
//        objectTag.setAttribute(new Attribute("class", c.getSimpleName()));
//        objectTag.setAttribute(new Attribute("id", getID(obj).toString()));

        // Additional Length attribute for array objects
//        Method m = c.getDeclaredMethod("size", (Class<?>) null);
//        Integer length = (Integer) m.invoke(obj, (Object) null);
//        objectTag.setAttribute(new Attribute("length", length.toString()));

        Element arrayObjectTag = createArrayTag(obj);

        Class compType = c.getComponentType();
        for(int i = 0; i < Array.getLength(obj); i++){
            Object value = Array.get(obj, i);
            if(value == null) {
                Element nullValue = createValueTag(null);
                arrayObjectTag.addContent(nullValue);
            }
            else if(compType.isPrimitive()){
                Element valueTag = createValueTag(value);
                arrayObjectTag.addContent(valueTag);
            }
            else{
                Element referenceTag = createReferenceTag(value);
                arrayObjectTag.addContent(referenceTag);
                serializeObject(value);
            }

        }
        return arrayObjectTag;
    }

    private Element createObjectTag(Object obj){
        Class c = obj.getClass();
        Element objectTag = new Element("object");
        objectTag.setAttribute(new Attribute("class", c.getSimpleName()));
        objectTag.setAttribute(new Attribute("id", getID(obj).toString()));
        return objectTag;
    }

    private Element createFieldTag(Field field){
        Element fieldTag = new Element("field");
        fieldTag.setAttribute(new Attribute("name", field.getName()));
        fieldTag.setAttribute(new Attribute("declaringClass", field.getDeclaringClass().getSimpleName()));
        return fieldTag;
    }

    private Element createValueTag(Object obj){
        Element valueTag = new Element("value");
        if(obj == null)
            valueTag.setText("null");
        else
            valueTag.setText(obj.toString());
        return valueTag;
    }

    private Element createReferenceTag(Object value){
        Element referenceTag = new Element("reference");
        referenceTag.setText(getID(value).toString());
        return referenceTag;
    }

    private Element createArrayTag(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class c = obj.getClass();
        Element objectTag = new Element("object");
        objectTag.setAttribute(new Attribute("class", c.getSimpleName()));
        objectTag.setAttribute(new Attribute("id", getID(obj).toString()));

        Method m = c.getDeclaredMethod("size", (Class<?>) null);
        Integer length = (Integer) m.invoke(obj, (Object) null);
        objectTag.setAttribute(new Attribute("length", length.toString()));
        return objectTag;
    }

    private Integer getID(Object obj) {
        if (!map.containsKey(obj)) {
            map.put(obj, id);
            id++;
        }
        return id;
    }
}
