package track.servlet.container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/*
Простейший сервлет, который принимает реквест и пишет текств респонз
Сервлет зарегистрирован в web.xml


servlet-name - по какому адресу идти на этот сервлет


<servlet-name>users</servlet-name>
<servlet-class>track.servlet.container.UsersServlet</servlet-class>

RUN:
$mvn jetty:run

BROWSER:
http://localhost:8080/users

 */
public class UsersServlet extends HttpServlet {


    // GET запросы
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Распарсим параметры урла и выведем их в ответ
        Map<String, String[]> params = request.getParameterMap();
        StringBuilder builder = new StringBuilder();
        params.forEach((key, val) -> builder.append(key).append("=").append(Arrays.toString(val)).append("\n"));

//
        List<User> users = new ArrayList<>();
        users.add(new User("Tom", "qwerty"));
        users.add(new User("Jerry", "123"));
//

        // За основу html документа берет шаблон из ресурсов webapp
        request.setAttribute("msg", "Hello!");
        request.setAttribute("allUsers", users);
        request.getRequestDispatcher("/hello.jsp").forward(request, response);



    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
