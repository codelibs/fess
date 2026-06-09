<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<section id="help-basic" class="help-section">
	<h2>Recherche de base</h2>
	<p>Pour effectuer une recherche, saisissez votre requête dans le champ de recherche et appuyez sur Entrée (ou cliquez sur le bouton Rechercher).</p>
	<p>Plusieurs mots sont combinés avec AND par défaut. Par exemple, <code>open source</code> trouve les documents contenant à la fois « open » et « source ».</p>
</section>
<section id="help-operators" class="help-section">
	<h2>Opérateurs de recherche</h2>
	<table class="table table-sm">
		<thead>
			<tr><th scope="col">Opérateur</th><th scope="col">Description</th><th scope="col">Exemple</th></tr>
		</thead>
		<tbody>
			<tr><td><code>AND</code> / <code>&amp;&amp;</code></td><td>Les deux termes doivent apparaître</td><td><code>fess AND search</code></td></tr>
			<tr><td><code>OR</code> / <code>||</code></td><td>L'un ou l'autre des termes peut apparaître</td><td><code>fess OR opensearch</code></td></tr>
			<tr><td><code>NOT</code> / <code>-</code></td><td>Exclure le terme</td><td><code>fess -lucene</code></td></tr>
			<tr><td><code>"..."</code></td><td>Expression exacte</td><td><code>"full text search"</code></td></tr>
			<tr><td><code>site:</code></td><td>Restreindre à un site</td><td><code>site:example.com fess</code></td></tr>
			<tr><td><code>filetype:</code></td><td>Restreindre à un type de fichier</td><td><code>filetype:pdf fess</code></td></tr>
		</tbody>
	</table>
</section>
<section id="help-wildcards" class="help-section">
	<h2>Caractères génériques</h2>
	<p>Vous pouvez utiliser <code>*</code> pour correspondre à n'importe quelle séquence de caractères et <code>?</code> pour correspondre à un seul caractère.</p>
	<p>Exemples : <code>fess*</code> correspond à « fess », « fessbook », « fessful ». <code>te?t</code> correspond à « test » ou « text ».</p>
</section>
<section id="help-ranges" class="help-section">
	<h2>Requêtes par plage</h2>
	<p>Utilisez des crochets pour rechercher une plage de valeurs : <code>content_length:[1024 TO 10240]</code> trouve les documents dont la taille est comprise entre 1Ko et 10Ko.</p>
	Les crochets <code>[ ]</code> incluent les bornes ; les accolades <code>{ }</code> les excluent, par exemple <code>content_length:{1024 TO 10240}</code> correspond aux tailles strictement supérieures à 1Ko et strictement inférieures à 10Ko.
</section>
<section id="help-facets" class="help-section">
	<h2>Filtres par facettes</h2>
	<p>Utilisez les facettes de la barre latérale pour affiner votre recherche par étiquette, période, taille de fichier ou type de fichier. Cliquez sur un élément de facette pour l'appliquer, et cliquez sur la pastille en haut des résultats pour le supprimer.</p>
</section>
<section id="help-shortcuts" class="help-section">
	<h2>Raccourcis clavier</h2>
	<ul>
		<li><code>/</code> &mdash; Mettre le focus sur le champ de recherche</li>
		<li><code>Esc</code> &mdash; Fermer les suggestions ou le modal</li>
		<li><code>Enter</code> &mdash; Lancer la recherche ou sélectionner une suggestion</li>
	</ul>
</section>
<section id="help-field" class="help-section">
	<h2>Recherche par champ</h2>
	<p>Vous pouvez rechercher dans n'importe quel champ en saisissant le nom du champ suivi d'un deux-points <code>:</code> puis du terme recherché. Par exemple, pour trouver des documents contenant « Fess » dans le titre :</p>
	<pre>title:Fess</pre>
	<p>Les champs disponibles incluent <code>url</code>, <code>host</code>, <code>site</code>, <code>title</code>, <code>content</code>, <code>content_length</code>, <code>last_modified</code> et <code>mimetype</code>. Ces champs sont personnalisables.</p>
</section>
<section id="help-sort" class="help-section">
	<h2>Tri</h2>
	<p>L'opérateur <code>sort:</code> trie les résultats selon un champ spécifié. Le format est <code>sort:&lt;field&gt;.&lt;order&gt;</code>, où <code>&lt;order&gt;</code> est <code>asc</code> ou <code>desc</code>. Par exemple, pour rechercher « Fess » trié par longueur de contenu de façon décroissante :</p>
	<pre>Fess sort:content_length.desc</pre>
	<p>Les champs de tri disponibles incluent <code>created</code>, <code>content_length</code> et <code>last_modified</code>. Ces champs sont personnalisables.</p>
</section>
<section id="help-boost" class="help-section">
	<h2>Pondération (Boost)</h2>
	<p>Pour pondérer un terme, utilisez le symbole <code>^</code> suivi d'un facteur de pondération (un nombre) à la fin du terme de recherche. Un facteur plus élevé augmente le score de pertinence de ce terme.</p>
	<pre>Fess^100</pre>
</section>
<section id="help-fuzzy" class="help-section">
	<h2>Recherche approximative</h2>
	<p>Pour effectuer une recherche approximative, utilisez le symbole <code>~</code> à la fin d'un seul mot. Le nombre optionnel après <code>~</code> (0 à 1) contrôle le seuil de similarité. La valeur par défaut est <code>0.5</code>.</p>
	<pre>Fess~0.5</pre>
</section>
