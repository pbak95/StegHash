<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
	<h1>Registration:</h1>
	<form:form method="POST" modelAttribute="userDTO">
		<table border="1">
			<tbody>
				<tr>
					<td>E-mail</td>
					<td><form:input type="text" path="email" /> <c:if
							test="${pageContext.request.method=='POST'}">
							<form:errors path="email" />
						</c:if></td>
				</tr>
				<tr>
					<td>Username</td>
					<td><form:input type="text" path="username" /> <c:if
							test="${pageContext.request.method=='POST'}">
							<form:errors path="username" />
						</c:if></td>
				</tr>
				<tr>
					<td>Password</td>
					<td><form:input type="text" path="password" /> <c:if
							test="${pageContext.request.method=='POST'}">
							<form:errors path="password" />
						</c:if></td>

				</tr>
				<tr>
					<td>Matching password</td>
					<td><form:input type="text" path="matchingPassword" /> <c:if
							test="${pageContext.request.method=='POST'}">
							<form:errors path="matchingPassword" />
						</c:if></td>

				</tr>
				<tr>
					<td colspan="2" align="right"><input type="submit"
						value="Register!" /></td>
				</tr>
			</tbody>
		</table>
	</form:form>
	<a href="login">Return to login page</a>
</body>
</html>