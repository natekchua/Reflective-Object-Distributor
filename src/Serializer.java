import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.w3c.dom.Attr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
        // todo: get list of objects fields
        Class c = obj.getClass();
        Field[] fields = c.getDeclaredFields();
        Element elem = new Element("object");
        elem.setAttribute(new Attribute("class", c.getSimpleName()));
        elem.setAttribute(new Attribute("id", getID(obj).toString()));

        for(Field field : fields){
            field.setAccessible(true);
            Element fieldElem = new Element("Field");
            fieldElem.setAttribute(new Attribute("name", field.getName()));
            fieldElem.setAttribute(new Attribute("declaringClass", field.getDeclaringClass().getSimpleName()));

            Object value = field.get(obj);
            Class type = field.getType();
            if(type.isPrimitive()){
                Element valElem = new Element("value");
                valElem.setText(value.toString());
                fieldElem.addContent(valElem);
            }
        }
        // todo: get value for each field (check if array)
    }

    private Integer getID(Object obj) {
        if (!map.containsKey(obj)) {
            map.put(obj, id);
            id++;
        }
        return 0;
    }
}
