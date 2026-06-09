<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<section id="help-basic" class="help-section">
	<h2>Búsqueda básica</h2>
	<p>Para buscar, escriba su consulta en el cuadro de búsqueda y presione Intro (o haga clic en el botón Buscar).</p>
	<p>De forma predeterminada, varias palabras se combinan con AND. Por ejemplo, <code>open source</code> encuentra documentos que contienen tanto "open" como "source".</p>
</section>
<section id="help-operators" class="help-section">
	<h2>Operadores de búsqueda</h2>
	<table class="table table-sm">
		<thead>
			<tr><th scope="col">Operador</th><th scope="col">Descripción</th><th scope="col">Ejemplo</th></tr>
		</thead>
		<tbody>
			<tr><td><code>AND</code> / <code>&amp;&amp;</code></td><td>Ambos términos deben aparecer</td><td><code>fess AND search</code></td></tr>
			<tr><td><code>OR</code> / <code>||</code></td><td>Puede aparecer cualquiera de los términos</td><td><code>fess OR opensearch</code></td></tr>
			<tr><td><code>NOT</code> / <code>-</code></td><td>Excluir el término</td><td><code>fess -lucene</code></td></tr>
			<tr><td><code>"..."</code></td><td>Frase exacta</td><td><code>"full text search"</code></td></tr>
			<tr><td><code>site:</code></td><td>Restringir a un sitio</td><td><code>site:example.com fess</code></td></tr>
			<tr><td><code>filetype:</code></td><td>Restringir a un tipo de archivo</td><td><code>filetype:pdf fess</code></td></tr>
		</tbody>
	</table>
</section>
<section id="help-wildcards" class="help-section">
	<h2>Comodines</h2>
	<p>Puede usar <code>*</code> para coincidir con cualquier secuencia de caracteres y <code>?</code> para coincidir con un solo carácter.</p>
	<p>Ejemplos: <code>fess*</code> coincide con "fess", "fessbook", "fessful". <code>te?t</code> coincide con "test" o "text".</p>
</section>
<section id="help-ranges" class="help-section">
	<h2>Consultas de rango</h2>
	<p>Use corchetes para buscar un rango de valores: <code>content_length:[1024 TO 10240]</code> encuentra documentos con tamaños entre 1KB y 10KB.</p>
	Los corchetes <code>[ ]</code> incluyen los límites; las llaves <code>{ }</code> los excluyen, por ejemplo <code>content_length:{1024 TO 10240}</code> coincide con tamaños estrictamente mayores que 1KB y estrictamente menores que 10KB.
</section>
<section id="help-facets" class="help-section">
	<h2>Filtros de facetas</h2>
	<p>Use las facetas de la barra lateral para acotar su búsqueda por etiqueta, período de tiempo, tamaño de archivo o tipo de archivo. Haga clic en un elemento de faceta para aplicarlo y haga clic en el chip en la parte superior de los resultados para eliminarlo.</p>
</section>
<section id="help-shortcuts" class="help-section">
	<h2>Atajos de teclado</h2>
	<ul>
		<li><code>/</code> &mdash; Enfocar el cuadro de búsqueda</li>
		<li><code>Esc</code> &mdash; Cerrar sugerencias o modal</li>
		<li><code>Enter</code> &mdash; Ejecutar búsqueda o seleccionar sugerencia</li>
	</ul>
</section>
<section id="help-field" class="help-section">
	<h2>Búsqueda por campo</h2>
	<p>Puede buscar en cualquier campo escribiendo el nombre del campo seguido de dos puntos <code>:</code> y luego el término que busca. Por ejemplo, para encontrar documentos con "Fess" en el título:</p>
	<pre>title:Fess</pre>
	<p>Los campos disponibles incluyen <code>url</code>, <code>host</code>, <code>site</code>, <code>title</code>, <code>content</code>, <code>content_length</code>, <code>last_modified</code> y <code>mimetype</code>. Estos son personalizables.</p>
</section>
<section id="help-sort" class="help-section">
	<h2>Ordenación</h2>
	<p>El operador <code>sort:</code> ordena los resultados por un campo especificado. El formato es <code>sort:&lt;field&gt;.&lt;order&gt;</code>, donde <code>&lt;order&gt;</code> es <code>asc</code> o <code>desc</code>. Por ejemplo, para buscar "Fess" ordenado por longitud de contenido de forma descendente:</p>
	<pre>Fess sort:content_length.desc</pre>
	<p>Los campos de ordenación disponibles incluyen <code>created</code>, <code>content_length</code> y <code>last_modified</code>. Estos son personalizables.</p>
</section>
<section id="help-boost" class="help-section">
	<h2>Boost</h2>
	<p>Para impulsar un término, use el símbolo <code>^</code> seguido de un factor de impulso (un número) al final del término de búsqueda. Un factor más alto aumenta la puntuación de relevancia de ese término.</p>
	<pre>Fess^100</pre>
</section>
<section id="help-fuzzy" class="help-section">
	<h2>Búsqueda difusa</h2>
	<p>Para realizar una búsqueda difusa, use el símbolo <code>~</code> al final de una sola palabra. El número opcional después de <code>~</code> (0 a 1) controla el umbral de similitud. El valor predeterminado es <code>0.5</code>.</p>
	<pre>Fess~0.5</pre>
</section>
