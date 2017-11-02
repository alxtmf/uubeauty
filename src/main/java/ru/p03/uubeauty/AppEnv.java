/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.p03.uubeauty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import ru.p03.common.util.QueriesEngine;
import ru.p03.uubeautyi.bot.document.spi.CustomDocumentMarshallerImpl;
import ru.p03.uubeautyi.bot.document.spi.DocumentMarshalerAggregator;
import ru.p03.uubeautyi.bot.document.spi.DocumentMarshaller;
import ru.p03.uubeauty.bot.info.MenuManager;
import ru.p03.uubeauty.bot.info.EmployeeManager;
import ru.p03.uubeauty.bot.info.ServiceManager;

import ru.p03.uubeauty.bot.schema.Action;
//import ru.p03.uubeauty.bot.schema.DataList;
import ru.p03.uubeauty.bot.schema.InfoMessageList;
import ru.p03.uubeauty.model.ClsDocType;
import ru.p03.uubeauty.model.repository.ClassifierRepository;
import ru.p03.uubeauty.model.repository.ClassifierRepositoryImpl;
import ru.p03.uubeautyi.bot.document.spi.JsonDocumentMarshallerImpl;

/**
 *
 * @author timofeevan
 */
public class AppEnv {
    
    public static String ROOT_PATH = "ROOT_PATH";
    public static String PROXY_HOST = "PROXY_HOST";
    public static String PROXY_PORT = "PROXY_PORT";
    public static String PROXY_USE = "PROXY_USE";   
    public static String SERVICE_FILE = "SERVICE_FILE";
    public static String EMPLOYEE_FILE = "EMPLOYEE_FILE";
    
    private Map environments = new HashMap();
    
    private static AppEnv CONTEXT;
    
    private DocumentMarshalerAggregator marshalFactory = new DocumentMarshalerAggregator();
    
    private ClassifierRepository classifierRepository =  new ClassifierRepositoryImpl();
    
    private ServiceManager serviceManager;
    private EmployeeManager employeeManager;
    private MenuManager menuManager;
    
    private StateHolder stateHolder;
    
    private AppEnv(){
        
    }
    
    private void initMarschaller(){
        DocumentMarshaller mrsh = new CustomDocumentMarshallerImpl(InfoMessageList.class, ClsDocType.SERVICE_INFO);
        DocumentMarshaller mrsh2 = new JsonDocumentMarshallerImpl(Action.class, ClsDocType.ACTION);
        DocumentMarshaller mrsh3 = new CustomDocumentMarshallerImpl(InfoMessageList.class, ClsDocType.EMPLOYEE_LIST);
//        DocumentMarshaller mrsh4 = new CustomDocumentMarshallerImpl(Man.class, ClsDocType.MAN);
        marshalFactory.setMarshallers(Arrays.asList(mrsh, mrsh2, mrsh3));//, mrsh3, mrsh4));
        marshalFactory.init();
    }
    
    private void initManagers(){
        //String filePathService = (String)environments.get(SERVICE_FILE);
        //String filePathEmployee = (String)environments.get(EMPLOYEE_FILE);
        //String xml;
        //try {
            //xml = ResourceUtils.readFile(filePathService);
            //InfoMessageList data = marshalFactory.<InfoMessageList>unmarshal(xml, ClsDocType.SERVICE_INFO);
            serviceManager = new ServiceManager(classifierRepository, marshalFactory, stateHolder);
            
            menuManager = new MenuManager(marshalFactory, stateHolder);
            
            //xml = ResourceUtils.readFile(filePathEmployee);
            //InfoMessageList dataInfoMessage = marshalFactory.<InfoMessageList>unmarshal(xml, ClsDocType.EMPLOYEE_LIST);
            employeeManager = new EmployeeManager(classifierRepository, marshalFactory, stateHolder);          
            
        //} catch (IOException ex) {
        //    Logger.getLogger(AppEnv.class.getName()).log(Level.SEVERE, null, ex);
        //}
        
    }
    
    private Properties initProperties(String propFileName){
        Properties properties = null;
        File fProp = null;
        String propDir = null;
        if (propFileName == null){
            propFileName = "conf.properties";
            propDir = "conf";

            fProp = new File(propDir + File.separator + propFileName);
        }else{
            fProp = new File(propFileName);
        }
        
        if (!fProp.exists()) {
            Logger.getLogger(AppEnv.class.getName()).log(Level.SEVERE, "not exists: " + fProp.getAbsolutePath());
            fProp = new File(".." + "/" + propDir + "/" + propFileName);
            if (!fProp.exists()) {
                Logger.getLogger(AppEnv.class.getName()).log(Level.SEVERE, "not exists: " + fProp.getAbsolutePath());
                fProp = new File("...." + "/" + propDir + "/" + propFileName);
                if (!fProp.exists()) {
                    Logger.getLogger(AppEnv.class.getName()).log(Level.SEVERE, "not exists: " + fProp.getAbsolutePath());
                    fProp = new File(propFileName);
                    Logger.getLogger(AppEnv.class.getName()).log(Level.SEVERE, "default: " + fProp.getAbsolutePath());
                }
            }
        }
        if (fProp.exists()) {
            try (InputStream fis = new FileInputStream(fProp);) {
                properties = new Properties();
                properties.load(new InputStreamReader(fis, Charset.forName("UTF-8")));
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    Logger.getLogger(AppEnv.class.getName()).log(Level.SEVERE, entry.getKey() + " = " + entry.getValue());
                }
                environments.putAll(properties);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AppEnv.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AppEnv.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return properties;
    }
    
    public ClassifierRepository getClassifierRepository() {
        return classifierRepository;
    }
    
    public ServiceManager getServiceManager(){
        return serviceManager;
    }
    
    public EmployeeManager getEmployeeManager(){
        return employeeManager;
    }
    
    public MenuManager getMenuManager(){
        return menuManager;
    }
    
//    public FindSnilsManager getFindSnilsManager(){
//        return findSnilsManager;
//    }
    
    public DocumentMarshalerAggregator getMarschaller(){
        return marshalFactory;
    } 
    
    public StateHolder getStateHolder(){
        return stateHolder;
    }
    
    public void init(String propFileName){
        initProperties(propFileName);
        initMarschaller();
        initManagers();
    }
    
    public void init(){
        initProperties(null);
        
        String db = "db";
        String dbUrl = "jdbc:h2:" + getRootPath() + File.separator + db + File.separator + "BEA;AUTO_SERVER=TRUE"; //<property name=\"javax.persistence.jdbc.url\" value=\
        Map hm = new HashMap();
        hm.put("javax.persistence.jdbc.url", dbUrl);
        
        QueriesEngine dao = QueriesEngine.instance("BEA", hm);
        
        ((ClassifierRepositoryImpl)getClassifierRepository()).setDAO(dao);
        
        stateHolder = new StateHolder();
        
        initMarschaller();
        initManagers();            
    }
    
    public static AppEnv getContext(String propFileName){ //https://habrahabr.ru/post/129494/
        if (CONTEXT == null){
            CONTEXT = new AppEnv();
            CONTEXT.init(propFileName);
        }
        return CONTEXT;
    }
    
    public static AppEnv getContext(){ //https://habrahabr.ru/post/129494/
        if (CONTEXT == null){
            CONTEXT = new AppEnv();
            CONTEXT.init();
        }
        return CONTEXT;
    }
    
    public String getRootPath(){
        return (String)environments.get(ROOT_PATH);
    }
    
    public HttpHost getProxyIfAbsetnt(){
        if (environments.get(PROXY_HOST) != null && environments.get(PROXY_PORT) != null 
                && environments.get(PROXY_USE) !=  null && "true".equalsIgnoreCase((String)environments.get(PROXY_USE))){
            try{
                HttpHost proxy = new HttpHost((String)environments.get(PROXY_HOST), Integer.valueOf((String)environments.get(PROXY_PORT)));
                return proxy;
            }catch (NumberFormatException ex) {
                Logger.getLogger(AppEnv.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}


