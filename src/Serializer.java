import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;

public class Serializer implements Serializable {

    private ElementTags eTags = new ElementTags();
    private IdentityHashMap<Object, Integer> map = new IdentityHashMap<>();
    private Integer id = 0;
    private Document doc;

    /*

     */
    public Document serialize(Object object) {

        Element serializedTag = new Element("serialized");  //Root element
        doc = new Document(serializedTag);
        serializeObject(object);

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
    private void serializeObject(Object obj) {
        Element objectTag = eTags.createObjectTag(obj, getID(obj));
        Class c = obj.getClass();
        Element root = doc.getRootElement();
        try{
            if (c.isArray())
                objectTag = serializeArray(obj);
            else
                serializeFields(obj, objectTag, c);

            root.addContent(objectTag);
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }

    /*

     */
    private void serializeFields(Object obj, Element objectTag, Class c) throws IllegalAccessException {
        Field [] fields = c.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            Element fieldTag = eTags.createFieldTag(field);

            Object value = field.get(obj);

            if (field.getType().isPrimitive()) {
                Element element = eTags.createValueTag(value);
                fieldTag.addContent(element);
            } else {
                Element referenceTag = eTags.createReferenceTag(getID(value).toString());
                fieldTag.addContent(referenceTag);
                serializeObject(value);
            }
            objectTag.addContent(fieldTag);
        }
    }

    /*

     */
    public Element serializeArray(Object obj) {
        Class c = obj.getClass();
        Class arrayType = c.getComponentType();
        Element objectTag = eTags.createArrayTag(obj, getID(obj));

        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object arrElement = Array.get(obj, i);
            if (arrayType.isPrimitive()) {
                Element valueTag = eTags.createValueTag(arrElement);
                objectTag.addContent(valueTag);
            }else if(arrElement == null) {
                Element nullElement = eTags.createValueTag(null);
                objectTag.addContent(nullElement);
            } else {
                Element referenceTag = eTags.createReferenceTag(getID(arrElement).toString());
                objectTag.addContent(referenceTag);
                serializeObject(arrElement);
            }
        }

        return objectTag;
    }

    /*

     */
    private Integer getID(Object obj) {
        int newID = id;
        if (!map.containsKey(obj)) {
            map.put(obj, newID);
            id++;
            return newID;
        }
        else
            return map.get(obj);
    }
}