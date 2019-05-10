<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<h2>검색방법</h2>
<dl>
	<dt>필드 지정 검색</dt>
	<dd>
		필드 이름 뒤에 콜론 :에 지정된 모든 필드에서 검색 할 수 있습니다.
		예를 들어, 문서의 title 필드에서 Fess가 포함 된 문서를 검색하려면 다음과 같이 입력합니다.
		<pre>title:Fess</pre>
		기본으로 사용 가능한 필드는 url, host, site, title content,
		content_length, last_modified 및 mimetype입니다.
		설정에서 지정하는 필드는 변경할 수 있습니다.
	</dd>
	<dt>정렬</dt>
	<dd>
		sort 연산자는 지정된 필드 이름으로 문서를 정렬합니다.
		sort 연산자의 사용 방법은 sort:&lt;field&gt;.&lt;order&gt;입니다.
		&lt;order&gt; 는 asc 또는 desc 오름차순 · 내림차순을 지정할 수 있습니다.
		예를 들어, Fess를 포함한 문서 크기의 내림차순으로 정렬하려면 다음과 같이 입력합니다.
		<pre>Fess sort:content_length.desc</pre>
		기본으로 사용 가능한 필드는 created, content_length 및 last_modified됩니다.
		설정에서 지정하는 필드는 변경할 수 있습니다.
	</dd>
	<dt>AND검색</dt>
	<dd>
		AND 연산자는 선택적 연결 연산자입니다.
		AND 연산자는 지정한 검색어를 모두 포함한 문서와 일치합니다.
		예를 들어, Fess과 CodeLibs를 포함하는 문서 검색하려면 다음과 같이 입력합니다.
		<pre>Fess AND CodeLibs</pre>
	</dd>
	</dd>
	<dt>OR검색</dt>
	<dd>
		OR 연산자는 지정한 검색어 중 하나를 포함하는 문서에 매치합니다.
		예를 들어, Fess 또는 CodeLibs를 포함하는 문서를 검색하려면 다음과 같이 입력합니다.
		<pre>Fess OR CodeLibs</pre>
	</dd>
	<dt>와일드카드</dt>
	<dd>
		검색어에 1 문자 이상의 문자 와일드 카드를 지원합니다.
		?는 단일 문자 와일드 카드로 지정할 수 *는 여러 문자 와일드 카드로 지정할 수 있습니다.
		<pre>Fess*</pre>
		또는
		<pre>Fe?s</pre>
		검색어의 첫 번째 문자는 사용할 수 없습니다.
	</dd>
	<dt>범위검색</dt>
	<dd>
		범위 검색 필드 값의 범위를 지정하여 그 조건에 일치하는 문서를 검색합니다.
		범위의 경계 값을 포함하려면 []를 사용하여 포함하지 않는 경우는 {}을 이용합니다.
		예를 들어, content_length 필드가 1000에서 10000의 값을 가진 문서를 검색하려면 다음과 같이 입력합니다.
		<pre>content_length:[1000 TO 10000]</pre>
	</dd>
	<dt>부스트(Boost)검색</dt>
	<dd>
		부스트 검색은 검색어 뒤에 ^을 지정하여 사용할 수 있습니다.
		^ 다음에 가중치 값을 정수로 지정하여 그 검색어가 향상됩니다.
		<pre>Fess^100</pre>
	</dd>
	<dt>퍼지(Fuzzy)검색</dt>
	<dd>
		퍼지 검색은 검색어 후 ~을 지정하여 사용할 수 있습니다.
		예를 들어, Fess가 포함 된 문서에 퍼지 검색을 수행하려면 다음과 같이 입력합니다.
		<pre>Fess~0.5</pre>
		~ 다음에 값을 1 이하로 지정할 수 있습니다.
		기본값은 0.5입니다.
	</dd>
</dl>
