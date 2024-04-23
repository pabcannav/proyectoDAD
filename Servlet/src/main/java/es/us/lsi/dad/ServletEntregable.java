package es.us.lsi.dad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ServletEntregable
 */
public class ServletEntregable extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Map<Integer, List<Double>> sensores; //idSensor , temperatura y humedad

	public void init() throws ServletException {
		sensores = new HashMap<>();
		sensores.put(1, valores(23.4,40.2)); // Sensor con id 1, tiene 23.4 grados y 40.2 % de humedad
		super.init();
	}

	private static List<Double> valores(Double temperatura, Double humedad) {
		List<Double> res = new ArrayList<>();
		res.add(temperatura);
		res.add(humedad);
		return res;
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServletEntregable() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// resp.getWriter().append("Served at: ").append(request.getContextPath());
		Integer idSensor = Integer.parseInt(request.getParameter("idSensor"));
		if (sensores.containsKey(idSensor)) {
			response(resp, "Datos en sensores para ese idSensor:\n"+
							"IdSensor:"+idSensor+",Temperatura:"+sensores.get(idSensor).get(0)+",Humedad:"+sensores.get(idSensor).get(1)+"%");
		} else {
			response(resp, "No hay nada en sensores para ese idSensor");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		BufferedReader reader = req.getReader();
		Gson gson = new Gson();
		DHT11 dht11 = gson.fromJson(reader, DHT11.class);
		
		sensores.put(dht11.getIdSensor(), dht11.getValores());
		resp.getWriter().println(gson.toJson(dht11));
		resp.setStatus(201);
		
	}

	private void response(HttpServletResponse resp, String msg) throws IOException {
		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("<t1>" + msg + "</t1>");
		out.println("</body>");
		out.println("</html>");
	}

}
