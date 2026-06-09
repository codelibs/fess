<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<section id="help-basic" class="help-section">
	<h2>基本的な検索</h2>
	<p>検索するには、検索ボックスにクエリを入力して Enter キーを押す（または「検索」ボタンをクリックする）だけです。</p>
	<p>複数の単語はデフォルトで AND で結合されます。例えば <code>オープン ソース</code> と入力すると、「オープン」と「ソース」の両方を含むドキュメントが見つかります。</p>
</section>
<section id="help-operators" class="help-section">
	<h2>検索演算子</h2>
	<table class="table table-sm">
		<thead>
			<tr><th scope="col">演算子</th><th scope="col">説明</th><th scope="col">例</th></tr>
		</thead>
		<tbody>
			<tr><td><code>AND</code> / <code>&amp;&amp;</code></td><td>両方の語を含む</td><td><code>fess AND search</code></td></tr>
			<tr><td><code>OR</code> / <code>||</code></td><td>いずれかの語を含む</td><td><code>fess OR opensearch</code></td></tr>
			<tr><td><code>NOT</code> / <code>-</code></td><td>その語を除外する</td><td><code>fess -lucene</code></td></tr>
			<tr><td><code>"..."</code></td><td>完全フレーズ一致</td><td><code>"全文検索"</code></td></tr>
			<tr><td><code>site:</code></td><td>サイトを限定する</td><td><code>site:example.com fess</code></td></tr>
			<tr><td><code>filetype:</code></td><td>ファイル種別を限定する</td><td><code>filetype:pdf fess</code></td></tr>
		</tbody>
	</table>
</section>
<section id="help-wildcards" class="help-section">
	<h2>ワイルドカード</h2>
	<p><code>*</code> は任意の文字列、<code>?</code> は任意の 1 文字にマッチします。</p>
	<p>例: <code>fess*</code> は "fess"、"fessbook"、"fessful" などにマッチします。<code>te?t</code> は "test" や "text" にマッチします。</p>
</section>
<section id="help-ranges" class="help-section">
	<h2>範囲検索</h2>
	<p>角括弧を使って値の範囲を指定できます: <code>content_length:[1024 TO 10240]</code> は 1KB〜10KB のファイルを検索します。</p>
	角括弧 <code>[ ]</code> は境界値を含み、波括弧 <code>{ }</code> は境界値を含みません。例えば <code>content_length:{1024 TO 10240}</code> は 1KB より大きく 10KB より小さいファイルを検索します。
</section>
<section id="help-facets" class="help-section">
	<h2>ファセットフィルター</h2>
	<p>サイドバーのファセットを使うと、ラベル、期間、ファイルサイズ、ファイル種別で検索を絞り込めます。ファセット項目をクリックして適用し、結果上部のチップをクリックして解除します。</p>
</section>
<section id="help-shortcuts" class="help-section">
	<h2>キーボードショートカット</h2>
	<ul>
		<li><code>/</code> &mdash; 検索ボックスにフォーカス</li>
		<li><code>Esc</code> &mdash; サジェストまたはモーダルを閉じる</li>
		<li><code>Enter</code> &mdash; 検索実行またはサジェスト選択</li>
	</ul>
</section>
<section id="help-field" class="help-section">
	<h2>フィールド指定検索</h2>
	<p>フィールド名の後にコロン <code>:</code> を付け、検索語を指定することで任意のフィールドを検索できます。たとえば、タイトルに「Fess」を含むドキュメントを検索する場合:</p>
	<pre>title:Fess</pre>
	<p>標準で利用可能なフィールドは <code>url</code>、<code>host</code>、<code>site</code>、<code>title</code>、<code>content</code>、<code>content_length</code>、<code>last_modified</code>、<code>mimetype</code> です。設定で変更できます。</p>
</section>
<section id="help-sort" class="help-section">
	<h2>ソート</h2>
	<p><code>sort:</code> 演算子は指定したフィールド名でドキュメントをソートします。利用方法は <code>sort:&lt;field&gt;.&lt;order&gt;</code> で、<code>&lt;order&gt;</code> は <code>asc</code>（昇順）または <code>desc</code>（降順）です。たとえば、「Fess」を含むドキュメントをコンテンツサイズの降順にソートする場合:</p>
	<pre>Fess sort:content_length.desc</pre>
	<p>標準で利用可能なソートフィールドは <code>created</code>、<code>content_length</code>、<code>last_modified</code> です。設定で変更できます。</p>
</section>
<section id="help-boost" class="help-section">
	<h2>ブースト検索</h2>
	<p>ブースト検索は検索語の後に <code>^</code> を付け、ブースト係数（数値）を指定することで利用できます。係数が大きいほど、その検索語の関連スコアが高くなります。</p>
	<pre>Fess^100</pre>
</section>
<section id="help-fuzzy" class="help-section">
	<h2>あいまい検索</h2>
	<p>あいまい検索は検索語の後に <code>~</code> を付けることで利用できます。<code>~</code> の後に 0〜1 の値で類似度を指定できます。デフォルトは <code>0.5</code> です。</p>
	<pre>Fess~0.5</pre>
</section>
