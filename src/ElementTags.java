import org.jdom2.Attribute;
import org.jdom2.Element;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public class ElementTags {
    /*

     */
    Element createObjectTag(Object obj, Integer id) {
        Element objectTag = new Element("object");
        objectTag.setAttribute(new Attribute("class", obj.getClass().getName()));
        objectTag.setAttribute(new Attribute("id", id.toString()));
        return objectTag;
    }

    /*

     */
    Element createArrayTag(Object obj, Integer id) {
        Element arrayTag = new Element("object");
        arrayTag.setAttribute(new Attribute("class", obj.getClass().getName()));
        arrayTag.setAttribute(new Attribute("id", id.toString()));
        arrayTag.setAttribute(new Attribute("length", Integer.toString(Array.getLength(obj))));
        return arrayTag;
    }

    /*

     */
    Element createFieldTag(Field field) {
        Element fieldTag = new Element("field");
        fieldTag.setAttribute(new Attribute("name", field.getName()));
        fieldTag.setAttribute(new Attribute("declaringClass", field.getDeclaringClass().getName()));
        return fieldTag;
    }

    /*

     */
    Element createValueTag(Object value) {
        Element valueTag = new Element("value");
        if (value == null)
            valueTag.setText("null");
        else
            valueTag.setText(value.toString());

        return valueTag;
    }

    /*

     */
    Element createReferenceTag(String id) {
        Element referenceTag = new Element("reference");
        referenceTag.setText(id);
        return referenceTag;
    }

}
