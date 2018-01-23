package com.example.buildsrc

import javassist.ClassPool
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtField
import javassist.Modifier

/**
 * Created by majun on 18/1/21.
 */

public class MyInject {

    private static ClassPool pool = ClassPool.getDefault()

    private static String injectStr = "System.out.println(\"插一下试试看\" ); ";

    public static void injectDir(String path, String packageName) {
        pool.appendClassPath(path)
        System.out.println("majun_path-------->:" + path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->

                String filePath = file.absolutePath
                System.out.println("majun_filePath-------->:" + filePath)
                //确保当前文件是class文件，并且不是系统自动生成的class文件
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")) {
                    changeMyClass()
                    createNewClass(path)
                    insertConstructor(filePath, packageName, path)
                }
            }
        }
    }

    private static void changeMyClass() {
        CtClass ctClass = pool.get("com.example.appgradle.MyClass")
        // 当CtClass对象通过writeFile()、toClass()、toBytecode()转化为Class后，Javassist冻结了CtClass对象，因此，JVM不允许再次加载Class文件，所以不允许对其修改。
        //因此，若想对CtClass对象进行修改，必须对其进行解冻，通过defrost()方法进行
        ctClass.defrost()
        ctClass.setSuperclass(pool.get("com.example.appgradle.FatherClass"))
        byte[] byteArr = ctClass.toBytecode();
        FileOutputStream fos = new FileOutputStream(new File("appgradle/build/intermediates/classes/debug/com/example/appgradle/MyClass" + ".class"));
        fos.write(byteArr)
        fos.close()
        //为了防止出现frozen class (cannot edit)的结果，需要最后调用detach();
        ctClass.detach()
    }

    //创建的时候有两种方式来写入文件
    private static void createNewClass(String path) {
        // 添加一个参数
        CtClass ctClass = pool.makeClass("com.example.appgradle.MyCC");
        CtField ctField = new CtField(CtClass.intType, "id", ctClass);
        ctField.setModifiers(Modifier.PUBLIC);
        ctClass.addField(ctField)

//        byte[] byteArr = ctClass.toBytecode();
//        FileOutputStream fos = new FileOutputStream(new File("appgradle/build/intermediates/classes/debug/com/example/appgradle/MyCC" +
//                ".class"));
//        fos.write(byteArr);
//        fos.close();
        ctClass.writeFile(path)
        System.out.println("Step One Over");
        // 解冻CtClass对象
        ctClass.defrost();

        // 为了测试ctClass是否能够再修改，再添加一个域
        CtField ctField2 = new CtField(pool.get("java.lang.String"), "name", ctClass);
        ctField2.setModifiers(Modifier.PUBLIC);
        ctClass.addField(ctField2);
        ctClass.setSuperclass(pool.get("com.example.appgradle.FatherClass"))
//        byteArr = ctClass.toBytecode();
//        fos = new FileOutputStream(new File("appgradle/build/intermediates/classes/debug/com/example/appgradle/MyCC" +
//                ".class"));
//        fos.write(byteArr);
//        fos.close();
        ctClass.writeFile(path)
        System.out.println("Step Two Over");
        ctClass.detach()
    }


    private static void insertConstructor(String filePath, String packageName, String path) {
        // 判断当前目录是否是在我们的应用包里面
        int index = filePath.indexOf(packageName);
        boolean isMyPackage = index != -1;
        if (isMyPackage) {
            int end = filePath.length() - 6 // .class = 6
            //由于Windows和mac的路径格式不一样，所以需要不同的方式替换
            String className = filePath.substring(index, end)
                    .replace('\\', '.').replace('/', '.')
            System.out.println("majun:--->" + className)
            //开始修改class文件
            CtClass c = pool.getCtClass(className)

            //首先判断是否需要解冻
            if (c.isFrozen()) {
                c.defrost()
            }

            //获取构造函数，如果没有的话，就新建
            CtConstructor[] cts = c.getDeclaredConstructors()
            if (cts == null || cts.length == 0) {
                //手动创建一个构造函数
                CtConstructor constructor = new CtConstructor(new CtClass[0], c)
                constructor.insertBeforeBody(injectStr)
                c.addConstructor(constructor)
            } else {
                cts[0].insertBeforeBody(injectStr)
            }
            c.writeFile(path)
            c.detach()
        }
    }
}
