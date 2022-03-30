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
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Eligijus
 */
public class Tickets extends HttpServlet {

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
        
        int index = 0;
        int[] id = new int[100];
        String[] names = new String[100];
        
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            Connection conn = null;
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Tickets</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Tickets at " + request.getContextPath() + "</h1>");
            String url="jdbc:derby://localhost:1527/";
            String dbName="ticket";
            try{
            DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
            conn = DriverManager.getConnection(url+dbName,"eligijus","eligijus");
            Statement select = conn.createStatement();
            ResultSet rs = select.executeQuery("SELECT * FROM TicketList WHERE NOT bought ");

            out.println("<table>" +
                        "  <tr>" +
                        "    <th>Ticket Name</th>" +
                        "    <th>Departure time</th>" +
                        "    <th>Arival time</th>" +
                        "    <th>Price</th>" +
                        "  </tr>");
            
            
            
            while(rs.next())
            {
              id[index] = Integer.parseInt(rs.getString(7));
              names[index] = rs.getString(2);
              out.println("<tr>");
              out.println("<td> " + rs.getString(2) +" </td>");
              out.println("<td> " + rs.getString(3) +" </td>");
              out.println("<td> " + rs.getString(4) +" </td>");
              out.println("<td> " + rs.getString(5) +" </td>");
              out.println("</tr>");
              index++;
            }
                        
            out.println("</table>");
            }
            catch (Exception e){  
            out.println(e);  
            } 

            //Servleto turinys matomas tik tam tikroms rolėms
            if (request.isUserInRole("User"))
            {
                out.println("<hr><br>");
                out.println("<h3>Seen just for user</h3>");
                out.println("<form name='BuyTicket' method='post' action='Tickets'>" +
                        "    <input type='hidden' name='Username' value='"+request.getRemoteUser()+"' /> <br/>" +
                        "    Select Ticket: <select name='TicketName'>");
                for (int i = 0; i < index; i++) {
                    out.println("<option value='"+id[i]+"'>"+names[i]+"</option>");
                }
                                
                out.println("    </select>" +
                        "    <input type='submit' value='Buy Ticket' />" +
                        "</form>");
                
                try{
                conn = DriverManager.getConnection(url+dbName,"eligijus","eligijus");
                Statement select = conn.createStatement();
                ResultSet rs = select.executeQuery("SELECT * FROM TicketList WHERE bought AND buyer = '"+request.getRemoteUser()+"' ");

                out.println("<h3>Bought tickets</h3>");
                out.println("<table>" +
                            "  <tr>" +
                            "    <th>Ticket Name</th>" +
                            "    <th>Departure time</th>" +
                            "    <th>Arival time</th>" +
                            "    <th>Price</th>" +
                            "  </tr>");



                while(rs.next())
                {
                  id[index] = Integer.parseInt(rs.getString(7));
                  names[index] = rs.getString(2);
                  out.println("<tr>");
                  out.println("<td> " + rs.getString(2) +" </td>");
                  out.println("<td> " + rs.getString(3) +" </td>");
                  out.println("<td> " + rs.getString(4) +" </td>");
                  out.println("<td> " + rs.getString(5) +" </td>");
                  out.println("</tr>");
                  index++;
                }

                out.println("</table>");
                }
                catch (Exception e){  
                out.println(e);  
                }
                
            }

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
        Connection conn=null;
        String url="jdbc:derby://localhost:1527/";
        String dbName="ticket";
        try{
            
            String username = request.getParameter("Username");  
            String ticketName = request.getParameter("TicketName");
            int id = Integer.parseInt(ticketName);
            
            DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
            conn = DriverManager.getConnection(url+dbName,"eligijus","eligijus");
            
            PreparedStatement pst =(PreparedStatement) conn.prepareStatement("UPDATE TicketList SET bought = ?, buyer = ? WHERE ID = ?");
          
            pst.setString(1,"true"); 
            pst.setString(2,username); 
            pst.setInt(3,id);        

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
