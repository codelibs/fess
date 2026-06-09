<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<section id="help-basic" class="help-section">
	<h2>Grundlegende Suche</h2>
	<p>Um zu suchen, geben Sie Ihre Anfrage in das Suchfeld ein und drücken Sie die Eingabetaste (oder klicken Sie auf die Schaltfläche „Suchen").</p>
	<p>Mehrere Wörter werden standardmäßig mit AND verknüpft. Beispielsweise findet <code>open source</code> Dokumente, die sowohl „open" als auch „source" enthalten.</p>
</section>
<section id="help-operators" class="help-section">
	<h2>Suchoperatoren</h2>
	<table class="table table-sm">
		<thead>
			<tr><th scope="col">Operator</th><th scope="col">Beschreibung</th><th scope="col">Beispiel</th></tr>
		</thead>
		<tbody>
			<tr><td><code>AND</code> / <code>&amp;&amp;</code></td><td>Beide Begriffe müssen vorkommen</td><td><code>fess AND search</code></td></tr>
			<tr><td><code>OR</code> / <code>||</code></td><td>Einer der Begriffe muss vorkommen</td><td><code>fess OR opensearch</code></td></tr>
			<tr><td><code>NOT</code> / <code>-</code></td><td>Den Begriff ausschließen</td><td><code>fess -lucene</code></td></tr>
			<tr><td><code>"..."</code></td><td>Genaue Phrase</td><td><code>"full text search"</code></td></tr>
			<tr><td><code>site:</code></td><td>Auf eine Website beschränken</td><td><code>site:example.com fess</code></td></tr>
			<tr><td><code>filetype:</code></td><td>Auf einen Dateityp beschränken</td><td><code>filetype:pdf fess</code></td></tr>
		</tbody>
	</table>
</section>
<section id="help-wildcards" class="help-section">
	<h2>Platzhalter</h2>
	<p>Sie können <code>*</code> verwenden, um eine beliebige Zeichenfolge zu entsprechen, und <code>?</code>, um ein einzelnes Zeichen zu entsprechen.</p>
	<p>Beispiele: <code>fess*</code> entspricht „fess", „fessbook", „fessful". <code>te?t</code> entspricht „test" oder „text".</p>
</section>
<section id="help-ranges" class="help-section">
	<h2>Bereichsabfragen</h2>
	<p>Verwenden Sie eckige Klammern, um nach einem Wertebereich zu suchen: <code>content_length:[1024 TO 10240]</code> findet Dokumente mit Größen zwischen 1KB und 10KB.</p>
	Eckige Klammern <code>[ ]</code> schließen die Grenzwerte ein; geschweifte Klammern <code>{ }</code> schließen sie aus, z. B. entspricht <code>content_length:{1024 TO 10240}</code> Größen, die strikt größer als 1KB und strikt kleiner als 10KB sind.
</section>
<section id="help-facets" class="help-section">
	<h2>Facettenfilter</h2>
	<p>Verwenden Sie die Facetten in der Seitenleiste, um Ihre Suche nach Label, Zeitraum, Dateigröße oder Dateityp einzuschränken. Klicken Sie auf ein Facettenelement, um es anzuwenden, und klicken Sie auf den Chip oben in den Ergebnissen, um es zu entfernen.</p>
</section>
<section id="help-shortcuts" class="help-section">
	<h2>Tastaturkürzel</h2>
	<ul>
		<li><code>/</code> &mdash; Suchfeld fokussieren</li>
		<li><code>Esc</code> &mdash; Vorschläge oder Modal schließen</li>
		<li><code>Enter</code> &mdash; Suche ausführen oder Vorschlag auswählen</li>
	</ul>
</section>
<section id="help-field" class="help-section">
	<h2>Feldsuche</h2>
	<p>Sie können jedes Feld durchsuchen, indem Sie den Feldnamen gefolgt von einem Doppelpunkt <code>:</code> und dem gesuchten Begriff eingeben. Beispiel: Um Dokumente zu finden, die „Fess" im Titel haben:</p>
	<pre>title:Fess</pre>
	<p>Zu den verfügbaren Feldern gehören <code>url</code>, <code>host</code>, <code>site</code>, <code>title</code>, <code>content</code>, <code>content_length</code>, <code>last_modified</code> und <code>mimetype</code>. Diese sind anpassbar.</p>
</section>
<section id="help-sort" class="help-section">
	<h2>Sortierung</h2>
	<p>Der Operator <code>sort:</code> sortiert Ergebnisse nach einem angegebenen Feld. Das Format ist <code>sort:&lt;field&gt;.&lt;order&gt;</code>, wobei <code>&lt;order&gt;</code> <code>asc</code> oder <code>desc</code> ist. Beispiel: Um nach „Fess" zu suchen und nach Inhaltslänge absteigend zu sortieren:</p>
	<pre>Fess sort:content_length.desc</pre>
	<p>Verfügbare Sortierfelder sind <code>created</code>, <code>content_length</code> und <code>last_modified</code>. Diese sind anpassbar.</p>
</section>
<section id="help-boost" class="help-section">
	<h2>Boosting</h2>
	<p>Um einen Begriff zu boosten, verwenden Sie das Symbol <code>^</code> gefolgt von einem Boost-Faktor (einer Zahl) am Ende des Suchbegriffs. Ein höherer Faktor erhöht den Relevanzscore dieses Begriffs.</p>
	<pre>Fess^100</pre>
</section>
<section id="help-fuzzy" class="help-section">
	<h2>Unscharfe Suche</h2>
	<p>Um eine unscharfe Suche durchzuführen, verwenden Sie das Symbol <code>~</code> am Ende eines einzelnen Worts. Die optionale Zahl nach <code>~</code> (0 bis 1) steuert den Ähnlichkeitsschwellenwert. Der Standardwert ist <code>0.5</code>.</p>
	<pre>Fess~0.5</pre>
</section>
