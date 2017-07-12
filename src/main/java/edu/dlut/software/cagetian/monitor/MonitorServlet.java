package edu.dlut.software.cagetian.monitor;

import net.sf.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by CageTian on 2017/7/12.
 */
@WebServlet(urlPatterns = "/MonitorServlet")
public class MonitorServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject s=MyThread.getNodeFileList();
        response.getWriter().write(s.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestDispatcher requestDispatcher=request.getRequestDispatcher("/index.jsp");
        requestDispatcher.forward(request,response);
    }
}
