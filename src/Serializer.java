import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class Serializer {

    private IdentityHashMap map = new IdentityHashMap<>();
    private Integer id = 0;

    /*

     */
    public Document serialize(ArrayList<Object> objects) throws IllegalAccessException {

        Element serializedTag = new Element("serialized");  //Root element
        Document doc = new Document(serializedTag);

        for(Object object : objects) {
            Element serializedObject = serializeObject(object, serializedTag, getID(object));
            serializedTag.addContent(serializedObject);
        }
        try{
            new XMLOutputter().output(doc, System.out);
            XMLOutputter xml = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            xml.setFormat(format);
            xml.output(doc, new FileWriter("serialized.xml"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return doc;
    }

    /*

     */
    public Element serializeObject(Object obj,Element root, Integer id) throws IllegalAccessException {
        Element objectTag = createObjectTag(obj);

        Class c = obj.getClass();
        Field[] fields = c.getDeclaredFields();

        for(Field field : fields){
            field.setAccessible(true);
            Element fieldTag = createFieldTag(field);
            objectTag.addContent(fieldTag);

            Class type = field.getType();
            Object value = field.get(obj);

            if(type.isArray())
                serializeArray(obj, root, type, value);
            else if(type.isPrimitive()){
                Element valueTag = createValueTag(value);
                fieldTag.addContent(valueTag);
            }else {
                id = getID(value);  //reference object ID
                Element referenceTag = createReferenceTag(value, id);
                fieldTag.addContent(referenceTag);

                Element refObjectTag = serializeObject(value, root, id);
                root.addContent(refObjectTag);
            }
        }
        return objectTag;
    }

    /*

     */
    private void serializeArray(Object obj, Element root, Class type, Object value) throws IllegalAccessException {
        Class arrayType = type.getComponentType();
        Element arrayTag = createArrayTag(type, value, getID(obj));
        root.addContent(arrayTag);

        if(arrayType.isPrimitive()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object arrElement = Array.get(value, i);
                Element valueTag = createValueTag(arrElement);
                arrayTag.addContent(valueTag);
            }
        }
        else{
            for (int i = 0; i < Array.getLength(value); i++) {
                Object refObject = Array.get(value, i);
                Integer id = getID(refObject);
                Element referenceTag = createReferenceTag(refObject, id);
                arrayTag.addContent(referenceTag);
                root.addContent(serializeObject(refObject, root, id));
            }
        }
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
    public Element createFieldTag(Field field) {
        Element fieldTag = new Element("field");
        fieldTag.setAttribute(new Attribute("name", field.getName()));
        fieldTag.setAttribute(new Attribute("declaringClass", field.getDeclaringClass().getSimpleName()));
        return fieldTag;
    }

    /*

     */
    public Element createArrayTag(Class type, Object value, Integer id) {
        Element arrayTag = new Element("object");
        arrayTag.setAttribute(new Attribute("class", type.getName()));
        arrayTag.setAttribute(new Attribute("id", getID(value).toString()));

        String length = String.valueOf(Array.getLength(value));
        arrayTag.setAttribute(new Attribute("length", length));
        return arrayTag;
    }

    /*

     */
    public Element createValueTag(Object value) {
        Element valueTag = new Element("value");
        valueTag.setText(value.toString());
        return valueTag;
    }

    /*

     */
    public Element createReferenceTag(Object value, Integer id) {
        Element referenceTag = new Element("Reference");
        referenceTag.setText(id.toString());
        return referenceTag;
    }

    /*

     */
    private Integer getID(Object obj) {
        if (!map.containsKey(obj)) {
            map.put(obj, id);
            id++;
        }
        return id;
    }
}