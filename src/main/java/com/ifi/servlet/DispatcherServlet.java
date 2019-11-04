package com.ifi.servlet;

import com.ifi.annotation.Controller;
import com.ifi.annotation.RequestMapping;
import com.ifi.controller.HelloController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/*", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    private Map<String, Method> uriMappings = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("Getting request for " + req.getRequestURI());
        // from prof
        var uri = req.getRequestURI();

        if(! uriMappings.containsKey(uri)){
            resp.sendError(404, "no mapping found for request uri " + uri);
            return;
        }

        var method = getMappingForUri(uri);
        try {
            // getting new instancea
            var instance = method.getDeclaringClass().newInstance();

            // getting params
            var params = req.getParameterMap();

            // calling method with params if needed
            Object result;
            if(method.getParameterCount() > 0 ){
                result = method.invoke(instance, params);
            }
            else {
                result = method.invoke(instance);
            }

            // sending response
            resp.getWriter().print(result.toString());
        } catch (InstantiationException | IllegalAccessException e) {
            // default exception handling
            e.printStackTrace();
        }
        catch( InvocationTargetException e){
            // when getting an exception, sending it to the client
            resp.sendError(500, "exception when calling method " + method.getName() + " : " + e.getCause().getMessage());
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // on enregistre notre controller au démarrage de la servlet
        this.registerController(HelloController.class);
    }

    protected void registerController(Class controllerClass) throws IllegalArgumentException{
        System.out.println("Analysing class " + controllerClass.getName());
        // DONE
        if(controllerClass.getAnnotation(Controller.class)==null){
            throw new IllegalArgumentException("This class isn't annotated with @Controller");
        }else{
            Method[] methodsArray = controllerClass.getDeclaredMethods();
            for(Method method: methodsArray){
                registerMethod(method);
            }
        }
    }

    protected void registerMethod(Method method) {
        System.out.println("Registering method " + method.getName());
        // DONE
        var requestMapping = method.getAnnotation(RequestMapping.class);
        if(!( requestMapping == null ) && !( method.getReturnType().equals(Void.class)) ){
            uriMappings.put(requestMapping.uri(), method );
        }
        return;
    }

    protected Map<String, Method> getMappings(){
        return this.uriMappings;
    }

    protected Method getMappingForUri(String uri){
        return this.uriMappings.get(uri);
    }
}