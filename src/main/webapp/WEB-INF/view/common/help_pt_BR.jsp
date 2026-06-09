<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<section id="help-basic" class="help-section">
	<h2>Pesquisa básica</h2>
	<p>Para pesquisar, digite sua consulta na caixa de pesquisa e pressione Enter (ou clique no botão Pesquisar).</p>
	<p>Várias palavras são combinadas com AND por padrão. Por exemplo, <code>open source</code> encontra documentos que contêm tanto "open" quanto "source".</p>
</section>
<section id="help-operators" class="help-section">
	<h2>Operadores de pesquisa</h2>
	<table class="table table-sm">
		<thead>
			<tr><th scope="col">Operador</th><th scope="col">Descrição</th><th scope="col">Exemplo</th></tr>
		</thead>
		<tbody>
			<tr><td><code>AND</code> / <code>&amp;&amp;</code></td><td>Ambos os termos devem aparecer</td><td><code>fess AND search</code></td></tr>
			<tr><td><code>OR</code> / <code>||</code></td><td>Qualquer um dos termos pode aparecer</td><td><code>fess OR opensearch</code></td></tr>
			<tr><td><code>NOT</code> / <code>-</code></td><td>Excluir o termo</td><td><code>fess -lucene</code></td></tr>
			<tr><td><code>"..."</code></td><td>Frase exata</td><td><code>"full text search"</code></td></tr>
			<tr><td><code>site:</code></td><td>Restringir a um site</td><td><code>site:example.com fess</code></td></tr>
			<tr><td><code>filetype:</code></td><td>Restringir a um tipo de arquivo</td><td><code>filetype:pdf fess</code></td></tr>
		</tbody>
	</table>
</section>
<section id="help-wildcards" class="help-section">
	<h2>Curingas</h2>
	<p>Você pode usar <code>*</code> para corresponder a qualquer sequência de caracteres e <code>?</code> para corresponder a um único caractere.</p>
	<p>Exemplos: <code>fess*</code> corresponde a "fess", "fessbook", "fessful". <code>te?t</code> corresponde a "test" ou "text".</p>
</section>
<section id="help-ranges" class="help-section">
	<h2>Consultas de intervalo</h2>
	<p>Use colchetes para pesquisar um intervalo de valores: <code>content_length:[1024 TO 10240]</code> encontra documentos com tamanhos entre 1KB e 10KB.</p>
	Os colchetes <code>[ ]</code> incluem os limites; as chaves <code>{ }</code> os excluem, por exemplo <code>content_length:{1024 TO 10240}</code> corresponde a tamanhos estritamente maiores que 1KB e estritamente menores que 10KB.
</section>
<section id="help-facets" class="help-section">
	<h2>Filtros de facetas</h2>
	<p>Use as facetas da barra lateral para refinar sua pesquisa por rótulo, período de tempo, tamanho de arquivo ou tipo de arquivo. Clique em um item de faceta para aplicá-lo e clique no chip no topo dos resultados para removê-lo.</p>
</section>
<section id="help-shortcuts" class="help-section">
	<h2>Atalhos de teclado</h2>
	<ul>
		<li><code>/</code> &mdash; Focar a caixa de pesquisa</li>
		<li><code>Esc</code> &mdash; Fechar sugestões ou modal</li>
		<li><code>Enter</code> &mdash; Executar pesquisa ou selecionar sugestão</li>
	</ul>
</section>
<section id="help-field" class="help-section">
	<h2>Pesquisa por campo</h2>
	<p>Você pode pesquisar em qualquer campo digitando o nome do campo seguido de dois pontos <code>:</code> e depois o termo que está procurando. Por exemplo, para encontrar documentos com "Fess" no título:</p>
	<pre>title:Fess</pre>
	<p>Os campos disponíveis incluem <code>url</code>, <code>host</code>, <code>site</code>, <code>title</code>, <code>content</code>, <code>content_length</code>, <code>last_modified</code> e <code>mimetype</code>. Esses campos são personalizáveis.</p>
</section>
<section id="help-sort" class="help-section">
	<h2>Ordenação</h2>
	<p>O operador <code>sort:</code> ordena os resultados por um campo especificado. O formato é <code>sort:&lt;field&gt;.&lt;order&gt;</code>, onde <code>&lt;order&gt;</code> é <code>asc</code> ou <code>desc</code>. Por exemplo, para pesquisar "Fess" ordenado por tamanho de conteúdo de forma decrescente:</p>
	<pre>Fess sort:content_length.desc</pre>
	<p>Os campos de ordenação disponíveis incluem <code>created</code>, <code>content_length</code> e <code>last_modified</code>. Esses campos são personalizáveis.</p>
</section>
<section id="help-boost" class="help-section">
	<h2>Boost</h2>
	<p>Para impulsionar um termo, use o símbolo <code>^</code> seguido de um fator de impulso (um número) no final do termo de pesquisa. Um fator mais alto aumenta a pontuação de relevância desse termo.</p>
	<pre>Fess^100</pre>
</section>
<section id="help-fuzzy" class="help-section">
	<h2>Pesquisa difusa</h2>
	<p>Para realizar uma pesquisa difusa, use o símbolo <code>~</code> no final de uma única palavra. O número opcional após <code>~</code> (0 a 1) controla o limiar de similaridade. O valor padrão é <code>0.5</code>.</p>
	<pre>Fess~0.5</pre>
</section>
