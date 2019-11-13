import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Attr;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;

public class Serializer implements Serializable {

    private IdentityHashMap<Object, Integer> map = new IdentityHashMap<>();
    private int id = 0;

    public Document serialize(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        Element serializedTag = new Element("serialized");
        Document doc = new Document(serializedTag);
        Class c = obj.getClass();

        if(c.isArray())
            serializedTag.addContent(serializeArray(obj));
        else
            serializedTag.addContent(serializeObject(obj));

        try{
            new XMLOutputter().output(doc, System.out);
            XMLOutputter xml = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            xml.setFormat(format);
            xml.output(doc, new FileWriter("serialized.xml"));
        }catch(IOException e){
            e.printStackTrace();
        }
        return doc;    //returns JDOM document
    }

    private Element serializeObject(Object obj) throws IllegalAccessException {
        Class c = obj.getClass();
        Field[] fields = c.getDeclaredFields();
        // Object Tag
        Element objectTag = createObjectTag(obj);

//        Element root = objectTag.getParentElement();
        // Field Tag
        for(Field field : fields){
            field.setAccessible(true);
            Element fieldTag = createFieldTag(field);

            Object value = field.get(obj);
            Class type = field.getType();

            if(type.isPrimitive()){
                Element valueTag = createValueTag(value);
                fieldTag.addContent(valueTag);
            }else{
                Element referenceTag = createReferenceTag(value);
                fieldTag.addContent(referenceTag);
                serializeObject(value);
            }
            objectTag.addContent(fieldTag);
        }
        return objectTag;
    }

    private Element serializeArray(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class c = obj.getClass();

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

    /*

     */
    private Element createObjectTag(Object obj){
        Class c = obj.getClass();
        Element objectTag = new Element("object");
        objectTag.setAttribute(new Attribute("class", c.getSimpleName()));
        objectTag.setAttribute(new Attribute("id", getID(obj).toString()));
        return objectTag;
    }

    /*

     */
    private Element createFieldTag(Field field){
        Element fieldTag = new Element("field");
        fieldTag.setAttribute(new Attribute("name", field.getName()));
        fieldTag.setAttribute(new Attribute("declaringClass", field.getDeclaringClass().getSimpleName()));
        return fieldTag;
    }

    /*

     */
    private Element createValueTag(Object obj){
        Element valueTag = new Element("value");
        if(obj == null)
            valueTag.setText("null");
        else
            valueTag.setText(obj.toString());
        return valueTag;
    }

    /*

     */
    private Element createReferenceTag(Object value){
        Element referenceTag = new Element("reference");
        referenceTag.setText(getID(value).toString());
        return referenceTag;
    }

    /*

     */
    private Element createArrayTag(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class c = obj.getClass();
        Element objectTag = new Element("object");
        objectTag.setAttribute(new Attribute("class", c.getSimpleName()));
        objectTag.setAttribute(new Attribute("id", getID(obj).toString()));

        Method m = c.getDeclaredMethod("length", (Class<?>) null);
        Integer length = (Integer) m.invoke(obj, (Object) null);
        objectTag.setAttribute(new Attribute("length", length.toString()));
        return objectTag;
    }

    /*

     */
    private Integer getID(Object obj) {
        if (!map.containsKey(obj)) {
            map.put(obj, id);
            id++;
        }
        else
            id = map.get(obj);

        return id;
    }
}
