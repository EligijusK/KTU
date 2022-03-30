/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.pool.*;
/**
 *
 * @author Eligijus
 */
public class SellTickets extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SellTickets</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SellTickets at " + request.getContextPath() + "</h1>");
            out.println("<form name='loginForm' method='post' action='SellTickets'>" +
                        "    <input type='hidden' name='Username' value='"+request.getRemoteUser()+"' /> <br/>" +
                        "    Ticket name: <input type='text' name='TicketName'/> <br/>" + 
                        "    Departure Time: <input type='datetime-local' name='Departure'/> <br/>" +
                        "    Arival Time: <input type='datetime-local' name='Arival'/> <br/>" +
                        "    Price: <input type='text' name='Price'/> <br/>" +
                        "    <input type='submit' value='Sell Ticket' />" +
                        "</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");  
        PrintWriter pw = response.getWriter(); 
        //String connectionURL = "jdbc:mysql://localhost:1527/Tickets";// newData is the database  
        Connection conn=null;
        String url="jdbc:derby://localhost:1527/";
        String dbName="ticket";
        
        try{  
          String username = request.getParameter("Username");  
          String ticketName = request.getParameter("TicketName");  
          String departureInfo = request.getParameter("Departure") + ":00";  
          String arivalInfo = request.getParameter("Arival") + ":00";  
          String price = request.getParameter("Price");  
          DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          Date date1 = df.parse(departureInfo);
          Date date2 = df.parse(arivalInfo);
          String departure = new SimpleDateFormat("YYYY-MM-dd HH:MM:SS.SSS").format(date1);
          String arival = new SimpleDateFormat("YYYY-MM-dd HH:MM:SS.SSS").format(date2);
          pw.println("<font size='6' color=blue>" + arival + "</font>");  
          DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
          conn = DriverManager.getConnection(url+dbName,"eligijus","eligijus");
          PreparedStatement pst =(PreparedStatement) conn.prepareStatement("insert into TicketList (owner,name,departuretime,ariwaltime,price,bought) values(?,?,?,?,?,?)");
          
          pst.setString(1,username);  
          pst.setString(2,ticketName);        
          pst.setString(3,departure);
          pst.setString(4,arival);
          pst.setString(5,price);
          pst.setString(6,"false");
          
          int i = pst.executeUpdate();
          conn.commit(); 
          String msg=" ";
          if(i!=0){  
            msg="Record has been inserted";
            pw.println("<font size='6' color=blue>" + msg + "</font>");  
          }  
          else{  
            msg="failed to insert the data";
            pw.println("<font size='6' color=blue>" + msg + "</font>");
           }  
          pst.close();
          
        }
        catch (Exception e){  
          pw.println(e);  
        }  
        
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
