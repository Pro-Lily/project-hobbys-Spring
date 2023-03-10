<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:forEach var="board" items="${bcno}">
	<article class="blog_item">

		<div class="blog_item_img">
			<img class="card-img rounded-0" height="350px"
				src="boardphotodownload?fileName=${board.bimage}"
				alt=""> <a href="javascript:boardDetails(${board.bno})"
				class="blog_item_date">
				<h3 style="text-align: center">
					<fmt:formatDate value="${board.bdate}" pattern="MM월 dd일" />
				</h3>
				
			</a>
		</div>
		<div class="blog_details">
			<a class="d-inline-block"
				href="javascript:boardDetails(${board.bno})">
				<h2 class="blog-head" style="color: #2d2d2d;">${board.btitle}</h2>
			</a>
			<p class="blog_list_content">${board.bcontent}</p>
		</div>
	</article>
</c:forEach>
