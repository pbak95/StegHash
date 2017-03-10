<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <title>Home Page</title>
</head>
   
<body>
<p>
      Hello <b><c:out value="${pageContext.request.remoteUser}"/></b><br>
      Roles: <b><sec:authentication property="principal.authorities" /></b>
    </p>
    
    <form action="logout" method="post">
      <input type="submit" value="Logout" />
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
</body>
</html>