<%@page pageEncoding="UTF-8" %>
              <div id="result">
                <div>
                  <c:forEach var="doc" varStatus="s" items="${documentItems}">
                    <div>
                      <a href="${doc.urlLink}"><span>${f:h(doc.contentTitle)}</span></a>
                      <span id="snip">
                        <br/>
                        <span style="color: #666666;">
                          ${doc.contentDescription}
                        </span>
                      </span>
                      <span style="color: #008000;">
                        <br/>
                        ${f:h(doc.site)}
                      </span>
                      <br/>
                    </div>
                    <br/>
                  </c:forEach>
                </div>
              </div>

              <div id="subfooter"  style="text-align: center;">
                <p>
                  <c:if test="${existPrevPage}">
                    <span>
                      <s:link href="prev?query=${f:u(query)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}">
                        <bean:message key="labels.prev_page"/>
                      </s:link>
                    </span>
                  </c:if>
                  <c:forEach var="pageNumber" varStatus="s" items="${pageNumberList}">
                    <c:if test="${pageNumber == currentPageNumber}">
                      <span>
                        ${pageNumber}
                      </span>
                    </c:if>
                    <c:if test="${pageNumber != currentPageNumber}">
                      <span>
                        <s:link href="move?query=${f:u(query)}&pn=${f:u(pageNumber)}&num=${f:u(pageSize)}">
                          ${f:h(pageNumber)}
                        </s:link>
                      </span>
                    </c:if>
                  </c:forEach>
                  <c:if test="${existNextPage}">
                    <span>
                      <s:link href="next?query=${f:u(query)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}">
                        <bean:message key="labels.next_page"/>
                      </s:link>
                    </span>
                  </c:if>
                </p>
              </div>
