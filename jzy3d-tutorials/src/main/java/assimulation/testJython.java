package assimulation;
//import javax.script.*;
//
//import org.python.core.PyFunction;
//import org.python.core.PyInteger;
//import org.python.core.PyObject;
//import org.python.util.PythonInterpreter;
import com.kenai.jaffl.struct.Struct;
import org.python.util.PythonInterpreter;
import org.python.core.*;
/**
 * Created by hongxiaoxiao on 17/1/3.
 */
public class testJython {
    public static void main(String[] args){
        //PythonInterpreter interp = new PythonInterpreter();
        //interp.execfile("test.py"); //运行test.py脚本
        //运行python命令
//        PythonInterpreter interp =
//                new PythonInterpreter();
//        System.out.println("Hello, brave new world");
//        interp.exec("import sys");
//        interp.exec("print sys");
//        interp.set("a", new PyInteger(42));
//        interp.exec("print a");
//        interp.exec("x = 2+2");
//        PyObject x = interp.get("x");
//        System.out.println("x: "+x);
//        System.out.println("Goodbye, cruel world");

        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile("/Users/hongxiaoxiao/Downloads/software/jzy3d-api-master/draw/draw.py");
        PyFunction func = (PyFunction)interpreter.get("myPlot",PyFunction.class);

        int [] a={1,2,3,4,5};
        int [] b={2,4,6,8,10};

        func.__call__(new PyArray(PyInteger.class,a), new PyArray(PyInteger.class,b));

//        int a = 2010, b = 2 ;
//        PyObject pyobj = func.__call__(new PyInteger(a), new PyInteger(b));
//        System.out.println("anwser = " + pyobj.toString());

//        try{
//            Process proc = Runtime.getRuntime().exec("/Users/hongxiaoxiao/Downloads/software/jzy3d-api-master/draw/draw.py");
//            proc.waitFor();
//        }catch (Exception e){
//        }
    }
}
