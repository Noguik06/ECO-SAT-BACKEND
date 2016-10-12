package com.uniandes.core;



import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;

import com.uniandes.common.Utility;
import com.uniandes.core.resources.DownloadFileServlet;
import com.uniandes.core.resources.EpisodeResource;
import com.uniandes.core.resources.FileResource;
import com.uniandes.core.resources.ProcedureResource;
import com.uniandes.core.resources.UploadFileServlet;
import com.uniandes.core.resources.UserResource;
import com.uniandes.db.dao.Tbl_CampoDAO;
import com.uniandes.db.dao.Tbl_FaseDAO;
import com.uniandes.db.dao.Tbl_TramiteDAO;
import com.uniandes.db.dao.Tbl_Tramite_UsuarioDAO;
import com.uniandes.db.dao.UsuarioDAO;
import com.uniandes.db.vo.Tbl_campo;
import com.uniandes.db.vo.Tbl_fase;
import com.uniandes.db.vo.Tbl_tramite;
import com.uniandes.db.vo.Tbl_tramite_usuario;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;


public class Main_App extends Application<LoginConfiguration> {

    public static void main(String[] args) throws Exception {
        new Main_App().run(args);
    }

    @Override
    public String getName() {
        return "ECOS_BACKEND";
    }

    
    private final HibernateBundle<LoginConfiguration> hibernate = new HibernateBundle<LoginConfiguration>(Tbl_tramite.class,
    		Tbl_fase.class, Tbl_campo.class, Tbl_tramite_usuario.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(LoginConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    
    
    @Override
    public void initialize(Bootstrap<LoginConfiguration> bootstrap) {
    	  bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(LoginConfiguration configuration, Environment environment) throws ClassNotFoundException {
    	
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");

        Utility.dao = jdbi;

        
        
        //Creamos los DAO que vamos a usar
        final UsuarioDAO dao = new UsuarioDAO(hibernate.getSessionFactory());
        final Tbl_TramiteDAO tbl_tramiteDAO = new Tbl_TramiteDAO(hibernate.getSessionFactory()) ;
        final Tbl_FaseDAO tbl_faseDAO = new Tbl_FaseDAO(hibernate.getSessionFactory()) ;
        final Tbl_CampoDAO tbl_campoDAO = new Tbl_CampoDAO(hibernate.getSessionFactory()) ;
        final Tbl_Tramite_UsuarioDAO tbl_Tramite_UsuarioDAO = new Tbl_Tramite_UsuarioDAO(hibernate.getSessionFactory()) ;
        
        
        final UserResource userResource = new UserResource(jdbi, dao);
        final EpisodeResource episodeResource = new EpisodeResource(jdbi);
        final FileResource fileResource = new FileResource(jdbi);
        final ProcedureResource tbl_tramiteResource = 
        		new ProcedureResource(tbl_tramiteDAO,tbl_faseDAO,tbl_campoDAO, tbl_Tramite_UsuarioDAO);


        
        //Registramos el primero recurso
        environment.jersey().register(userResource);
        //Registramos el segundo recurso
        environment.jersey().register(episodeResource);
        //Registros el tercer recurso
        environment.jersey().register(fileResource);
        //Registramos el servicio de los tramites
        environment.jersey().register(tbl_tramiteResource);
        //Añadimos el servlet para subir archivos
        ServletHolder fileUploadServletHolder = new ServletHolder(new UploadFileServlet());
        fileUploadServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        environment.getApplicationContext().addServlet(fileUploadServletHolder, "/fileResource/uploadFileServlet");
        //Añadimos el servlet para descargar los archivos
        environment.servlets().addServlet("DownloadFileServlet", DownloadFileServlet.class).addMapping("/fileResource/downloadFileServlet");
        
    }
}
