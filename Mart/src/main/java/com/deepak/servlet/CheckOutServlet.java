package com.deepak.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.deepak.connection.DBCon;
import com.deepak.dao.OrderDao;
import com.deepak.model.Cart;
import com.deepak.model.Order;
import com.deepak.model.User;

@WebServlet("/CheckOutServlet")
public class CheckOutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try(PrintWriter out = response.getWriter()) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			ArrayList<Cart> cart_list = (ArrayList<Cart>) request.getSession().getAttribute("cart-list");
			User auth = (User) request.getSession().getAttribute("auth");

			if (auth == null) {
				response.sendRedirect("login.jsp");
				return; // Ensure no further processing happens
			}

			if (cart_list == null || cart_list.isEmpty()) {
				response.sendRedirect("cart.jsp");
				return; // Ensure no further processing happens
			}

			OrderDao oDao = new OrderDao(DBCon.getConnection());
			boolean allOrdersInserted = true;

			for (Cart c : cart_list) {
				Order order = new Order();
				order.setId(c.getId());
				order.setUid(auth.getId());
				order.setQunatity(c.getQuantity());
				order.setDate(formatter.format(date));

				boolean result = oDao.insertOrder(order);
				if (!result) {
					allOrdersInserted = false;
					break;
				}
			}

			if (allOrdersInserted) {
				cart_list.clear();
				response.sendRedirect("orders.jsp");
			} else {
				out.println("Order insertion failed, please try again.");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
