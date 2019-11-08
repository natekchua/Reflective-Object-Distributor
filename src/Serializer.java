import org.jdom2.Document;
import org.jdom2.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

    private void serializeObject(Object obj) {
        // todo: get list of objects fields
        // todo: get value for each field (check if array)
    }
}
