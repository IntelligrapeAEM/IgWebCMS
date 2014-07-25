<%--
  Toolbar component.

  Toolbar component for adding links above Search box.

--%>
<%@include file="/apps/ig-igwebcms/global.jsp"%>
<%@page session="false" %>
<%@taglib uri="http://ig.com/igwebcms" prefix="toolbar" %>
<toolbar:createlinks />
<ul>
    <c:forEach items="${linkProperties}" var="link" varStatus="loopCounter">
        <c:choose>
            <c:when test="${not empty link.url}">
                <c:choose>
                    <c:when test="${properties.saperator}">
                        <c:choose>
                            <c:when test="${loopCounter.count ==1}">
                                <li><a href="${link.url}" target="${link.target}">${link.linkText}</a></li>
                            </c:when>
                            <c:otherwise>
                                <li class="saperator"><a href="${link.url}" target="${link.target}">${link.linkText}</a></li>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${link.url}" target="${link.target}">${link.linkText}</a></li>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${properties.saperator}">
                        <c:choose>
                            <c:when test="${loopCounter.count ==1}">
                                <li>${link.linkText}</li>
                            </c:when>
                            <c:otherwise>
                                <li class="saperator">${link.linkText}</li>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <li>${link.linkText}</li>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</ul>
<c:if test="${fn:length(linkProperties)==0}">
    No Links added .
</c:if>