<!DOCTYPE html>
<html>
<head>
<title>Hello JAX-RS</title>
</head>
<body>
	<div>
		<div>
			<h2>Hello JAX-RS</h2>

			<table width="750" frame="below">
				<tr>
					<th align="left" width="200">Java Property</th>
					<th align="left" width="500">Value</th>
				</tr>
				<%@ page import="java.util.*"%>
				<%
                  ArrayList<String> mainPageProps = new ArrayList<String>();
                  mainPageProps.add("java.version");
                  mainPageProps.add("java.vendor");
                  mainPageProps.add("os.arch");
                  mainPageProps.add("catalina.base");
                  mainPageProps.add("jetty.base");
                  mainPageProps.add("user.timezone");
                  for (String name : mainPageProps) {
                    String value = System.getProperty(name);
                    if (value != null) {
					  out.print("<tr><td>" + name);
					  out.print("</td><td>" + value );
					  out.print("</td></tr>");
                    }
                  }
              %>
			</table>
		</div>
	</div>
</body>

</html>