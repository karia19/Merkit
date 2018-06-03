package merkit.lista;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {

    public static void main(String[] args) throws Exception{
        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }
        System.out.println("Hello");


        Spark.get("/autot", (Request reg, Response res)-> {

            List<String> autot = new ArrayList<>();
            //Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:autot.db");
            PreparedStatement stm = connection.prepareStatement("SELECT malli FROM Merkit");

            ResultSet tulos = stm.executeQuery();

            while(tulos.next()) {
                String malli = tulos.getString("malli");
                autot.add(malli);
            }
            connection.close();

            HashMap map = new HashMap<>();
            map.put("lista", autot);

            return new ModelAndView(map, "OmaTesti");


        }, new ThymeleafTemplateEngine());

        Spark.post("/autot",(reg, res)->{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:autot.db");
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Merkit (malli) VALUES (?)");
            stm.setString(1, reg.queryParams("nimi"));
            stm.executeUpdate();

            connection.close();

            res.redirect("/autot");
            return "";

        });
    }
}
