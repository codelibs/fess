# Fess検索画面 Next.js実装 設計書

## 1. アーキテクチャ設計

### 1.1 全体アーキテクチャ

```
┌─────────────────────────────────────────────────────────┐
│                  Next.js Application                     │
│  ┌───────────────────────────────────────────────────┐  │
│  │              App Router (Next.js 14+)              │  │
│  │  ┌─────────────────────────────────────────────┐  │  │
│  │  │  Pages (app/)                               │  │  │
│  │  │  - page.tsx (index)                         │  │  │
│  │  │  - search/page.tsx                          │  │  │
│  │  │  - advance/page.tsx                         │  │  │
│  │  │  - login/page.tsx                           │  │  │
│  │  │  - help/page.tsx                            │  │  │
│  │  └─────────────────────────────────────────────┘  │  │
│  │  ┌─────────────────────────────────────────────┐  │  │
│  │  │  Components (components/)                   │  │  │
│  │  │  - Layout Components                        │  │  │
│  │  │  - Search Components                        │  │  │
│  │  │  - UI Components                            │  │  │
│  │  └─────────────────────────────────────────────┘  │  │
│  │  ┌─────────────────────────────────────────────┐  │  │
│  │  │  Hooks (hooks/)                             │  │  │
│  │  │  - useSearch, useSuggest, useFavorite, etc. │  │  │
│  │  └─────────────────────────────────────────────┘  │  │
│  │  ┌─────────────────────────────────────────────┐  │  │
│  │  │  Services (services/)                       │  │  │
│  │  │  - API Client, Auth, etc.                   │  │  │
│  │  └─────────────────────────────────────────────┘  │  │
│  │  ┌─────────────────────────────────────────────┐  │  │
│  │  │  State Management (store/ or context/)      │  │  │
│  │  │  - Search State, User State, etc.           │  │  │
│  │  └─────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                         │
                         │ HTTP/HTTPS
                         ▼
┌─────────────────────────────────────────────────────────┐
│                   Fess Server                            │
│  ┌───────────────────────────────────────────────────┐  │
│  │          OpenAPI v1 (/api/v1/*)                   │  │
│  │  - GET /documents (検索)                          │  │
│  │  - GET /labels (ラベル一覧)                       │  │
│  │  - GET /popular-words (人気ワード)                │  │
│  │  - GET /suggest-words (サジェスト)                │  │
│  │  - GET /favorites (お気に入り一覧)                │  │
│  │  - POST /documents/{docId}/favorite               │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 1.2 技術スタック詳細

| カテゴリ | 技術 | バージョン | 用途 |
|---------|------|----------|------|
| フレームワーク | Next.js | 14.x | Reactフレームワーク（App Router） |
| 言語 | TypeScript | 5.x | 型安全な開発 |
| UIライブラリ | React | 18.x | コンポーネントベースUI |
| スタイリング | CSS Modules / Tailwind CSS | 3.x | スタイル管理 |
| 状態管理 | React Context / Zustand | - | グローバル状態管理 |
| HTTPクライアント | fetch / axios | - | API通信 |
| フォーム管理 | React Hook Form | 7.x | フォームバリデーション |
| バリデーション | Zod | 3.x | スキーマバリデーション |
| 国際化 | next-intl | 3.x | i18n対応 |
| テスト | Jest + React Testing Library | - | 単体テスト |
| E2Eテスト | Playwright | - | 統合テスト |
| Linter | ESLint | - | コード品質 |
| Formatter | Prettier | - | コードフォーマット |

---

## 2. ディレクトリ構成

```
fess-nextjs/
├── public/                          # 静的ファイル
│   ├── images/
│   │   ├── logo.png
│   │   ├── logo-head.png
│   │   ├── logo-top.png
│   │   ├── blank.png
│   │   └── loading.gif
│   └── fonts/
├── src/
│   ├── app/                         # Next.js App Router
│   │   ├── layout.tsx               # ルートレイアウト
│   │   ├── page.tsx                 # トップページ (/)
│   │   ├── search/
│   │   │   ├── page.tsx             # 検索結果ページ (/search)
│   │   │   └── layout.tsx           # 検索レイアウト
│   │   ├── advance/
│   │   │   └── page.tsx             # 詳細検索ページ (/advance)
│   │   ├── login/
│   │   │   └── page.tsx             # ログインページ (/login)
│   │   ├── help/
│   │   │   └── page.tsx             # ヘルプページ (/help)
│   │   ├── api/                     # API Routes（必要に応じて）
│   │   │   └── proxy/               # プロキシエンドポイント
│   │   └── globals.css              # グローバルCSS
│   ├── components/                  # Reactコンポーネント
│   │   ├── layout/
│   │   │   ├── Header.tsx           # ヘッダーコンポーネント
│   │   │   ├── Footer.tsx           # フッターコンポーネント
│   │   │   ├── UserMenu.tsx         # ユーザーメニュー
│   │   │   └── Notifications.tsx    # 通知バナー
│   │   ├── search/
│   │   │   ├── SearchBox.tsx        # 検索ボックス
│   │   │   ├── SearchOptions.tsx    # 検索オプション
│   │   │   ├── SearchResults.tsx    # 検索結果一覧
│   │   │   ├── DocumentItem.tsx     # 検索結果アイテム
│   │   │   ├── Pagination.tsx       # ページネーション
│   │   │   ├── Facets.tsx           # ファセット
│   │   │   ├── SortBadge.tsx        # ソート表示バッジ
│   │   │   ├── PopularWords.tsx     # 人気ワード
│   │   │   ├── RelatedQueries.tsx   # 関連クエリ
│   │   │   └── NoResults.tsx        # 検索結果なし
│   │   ├── suggest/
│   │   │   └── Suggestor.tsx        # サジェスト機能
│   │   ├── advance/
│   │   │   └── AdvancedSearchForm.tsx # 詳細検索フォーム
│   │   ├── login/
│   │   │   └── LoginForm.tsx        # ログインフォーム
│   │   └── ui/                      # 汎用UIコンポーネント
│   │       ├── Button.tsx
│   │       ├── Input.tsx
│   │       ├── Select.tsx
│   │       ├── Badge.tsx
│   │       ├── Dropdown.tsx
│   │       └── Modal.tsx
│   ├── hooks/                       # カスタムフック
│   │   ├── useSearch.ts             # 検索フック
│   │   ├── useSuggest.ts            # サジェストフック
│   │   ├── useFavorite.ts           # お気に入りフック
│   │   ├── usePopularWords.ts       # 人気ワードフック
│   │   ├── useLabels.ts             # ラベルフック
│   │   ├── useDebounce.ts           # デバウンスフック
│   │   └── useLocalStorage.ts       # ローカルストレージフック
│   ├── services/                    # APIサービス
│   │   ├── api/
│   │   │   ├── client.ts            # APIクライアント（axios/fetch設定）
│   │   │   ├── search.ts            # 検索API
│   │   │   ├── labels.ts            # ラベルAPI
│   │   │   ├── popularWords.ts      # 人気ワードAPI
│   │   │   ├── suggest.ts           # サジェストAPI
│   │   │   └── favorite.ts          # お気に入りAPI
│   │   └── auth.ts                  # 認証サービス
│   ├── types/                       # TypeScript型定義
│   │   ├── api.ts                   # API型定義
│   │   ├── search.ts                # 検索関連型定義
│   │   ├── user.ts                  # ユーザー型定義
│   │   └── common.ts                # 共通型定義
│   ├── utils/                       # ユーティリティ関数
│   │   ├── format.ts                # フォーマット関数
│   │   ├── validation.ts            # バリデーション
│   │   ├── queryBuilder.ts          # クエリ構築
│   │   └── constants.ts             # 定数定義
│   ├── store/                       # 状態管理（Zustand使用の場合）
│   │   ├── searchStore.ts           # 検索状態
│   │   ├── userStore.ts             # ユーザー状態
│   │   └── uiStore.ts               # UI状態
│   ├── context/                     # React Context（Context API使用の場合）
│   │   ├── SearchContext.tsx
│   │   ├── UserContext.tsx
│   │   └── I18nContext.tsx
│   └── i18n/                        # 国際化
│       ├── locales/
│       │   ├── en.json
│       │   ├── ja.json
│       │   └── ... (17言語)
│       └── config.ts
├── .env.local                       # 環境変数（ローカル）
├── .env.production                  # 環境変数（本番）
├── next.config.js                   # Next.js設定
├── tsconfig.json                    # TypeScript設定
├── tailwind.config.ts               # Tailwind CSS設定
├── package.json
└── README.md
```

---

## 3. コンポーネント設計

### 3.1 ページコンポーネント

#### 3.1.1 トップページ (`app/page.tsx`)
```typescript
// app/page.tsx
import { SearchBox } from '@/components/search/SearchBox';
import { PopularWords } from '@/components/search/PopularWords';
import { SearchOptions } from '@/components/search/SearchOptions';

export default async function IndexPage() {
  const popularWords = await fetchPopularWords();

  return (
    <main className="index-page">
      <div className="logo-container">
        <img src="/images/logo.png" alt="Fess" />
      </div>

      <SearchBox
        autoFocus
        showSuggest
        onSubmit={handleSearch}
      />

      <PopularWords words={popularWords} />

      <SearchOptions collapsible />
    </main>
  );
}
```

#### 3.1.2 検索結果ページ (`app/search/page.tsx`)
```typescript
// app/search/page.tsx
import { SearchResults } from '@/components/search/SearchResults';
import { Facets } from '@/components/search/Facets';

export default async function SearchPage({ searchParams }) {
  const results = await searchDocuments(searchParams);

  return (
    <main className="search-page">
      <SearchOptions compact />

      <div className="search-status">
        <SortBadge sort={results.sort} />
        <NumBadge num={results.pageSize} />
        <LangBadge lang={results.lang} />
      </div>

      <PopularWords words={results.popularWords} />
      <RelatedQueries queries={results.relatedQueries} />

      {results.recordCount > 0 ? (
        <>
          <SearchResults documents={results.data} />
          <Facets facets={results.facetResponse} />
          <Pagination {...results} />
        </>
      ) : (
        <NoResults query={results.q} />
      )}
    </main>
  );
}
```

#### 3.1.3 詳細検索ページ (`app/advance/page.tsx`)
```typescript
// app/advance/page.tsx
import { AdvancedSearchForm } from '@/components/advance/AdvancedSearchForm';

export default function AdvancePage() {
  return (
    <main className="advance-page">
      <h2>Advanced Search</h2>
      <AdvancedSearchForm />
    </main>
  );
}
```

### 3.2 レイアウトコンポーネント

#### 3.2.1 ヘッダー (`components/layout/Header.tsx`)
```typescript
// components/layout/Header.tsx
'use client';

import { useState } from 'react';
import { SearchBox } from '@/components/search/SearchBox';
import { UserMenu } from './UserMenu';

interface HeaderProps {
  showSearch?: boolean;
  user?: UserInfo;
}

export function Header({ showSearch = false, user }: HeaderProps) {
  const [showOptions, setShowOptions] = useState(false);

  return (
    <header className="header">
      <nav className="navbar">
        <div className="container">
          <a href="/" className="logo">
            <img src="/images/logo-head.png" alt="Fess" />
          </a>

          {showSearch && (
            <div className="search-container">
              <SearchBox compact />
              <button onClick={() => setShowOptions(!showOptions)}>
                Options
              </button>
            </div>
          )}

          <div className="nav-menu">
            {user ? (
              <UserMenu user={user} />
            ) : (
              <a href="/login">Login</a>
            )}
            <a href="/help">Help</a>
          </div>
        </div>
      </nav>

      {showSearch && showOptions && (
        <SearchOptions onClose={() => setShowOptions(false)} />
      )}
    </header>
  );
}
```

### 3.3 検索コンポーネント

#### 3.3.1 検索ボックス (`components/search/SearchBox.tsx`)
```typescript
// components/search/SearchBox.tsx
'use client';

import { useState, useRef } from 'react';
import { useSuggest } from '@/hooks/useSuggest';
import { Suggestor } from '@/components/suggest/Suggestor';

interface SearchBoxProps {
  initialValue?: string;
  autoFocus?: boolean;
  compact?: boolean;
  showSuggest?: boolean;
  onSubmit?: (query: string) => void;
}

export function SearchBox({
  initialValue = '',
  autoFocus = false,
  compact = false,
  showSuggest = true,
  onSubmit
}: SearchBoxProps) {
  const [query, setQuery] = useState(initialValue);
  const inputRef = useRef<HTMLInputElement>(null);

  const { suggestions, isLoading } = useSuggest(query);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit?.(query);
  };

  return (
    <form onSubmit={handleSubmit} className="search-box">
      <div className="input-group">
        <input
          ref={inputRef}
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          maxLength={1000}
          autoFocus={autoFocus}
          autoComplete="off"
          className="search-input"
        />

        {showSuggest && (
          <Suggestor
            suggestions={suggestions}
            isLoading={isLoading}
            onSelect={(value) => {
              setQuery(value);
              onSubmit?.(value);
            }}
          />
        )}
      </div>

      <button type="submit" className="search-button">
        Search
      </button>
    </form>
  );
}
```

#### 3.3.2 サジェスト (`components/suggest/Suggestor.tsx`)
```typescript
// components/suggest/Suggestor.tsx
'use client';

import { useState, useEffect, useRef } from 'react';
import { useKeyboard } from '@/hooks/useKeyboard';

interface SuggestorProps {
  suggestions: string[];
  isLoading?: boolean;
  onSelect: (value: string) => void;
}

export function Suggestor({ suggestions, isLoading, onSelect }: SuggestorProps) {
  const [selectedIndex, setSelectedIndex] = useState(-1);
  const [isVisible, setIsVisible] = useState(false);
  const listRef = useRef<HTMLUListElement>(null);

  useKeyboard({
    onArrowUp: () => setSelectedIndex((prev) => Math.max(prev - 1, -1)),
    onArrowDown: () => setSelectedIndex((prev) => Math.min(prev + 1, suggestions.length - 1)),
    onEnter: () => {
      if (selectedIndex >= 0) {
        onSelect(suggestions[selectedIndex]);
      }
    }
  });

  useEffect(() => {
    setIsVisible(suggestions.length > 0);
  }, [suggestions]);

  if (!isVisible) return null;

  return (
    <ul ref={listRef} className="suggest-list">
      {suggestions.map((suggestion, index) => (
        <li
          key={index}
          className={index === selectedIndex ? 'selected' : ''}
          onClick={() => onSelect(suggestion)}
          onMouseEnter={() => setSelectedIndex(index)}
        >
          {suggestion}
        </li>
      ))}
    </ul>
  );
}
```

#### 3.3.3 検索結果アイテム (`components/search/DocumentItem.tsx`)
```typescript
// components/search/DocumentItem.tsx
'use client';

import { useState } from 'react';
import { useFavorite } from '@/hooks/useFavorite';
import { Thumbnail } from './Thumbnail';

interface DocumentItemProps {
  document: DocumentItem;
  queryId: string;
  index: number;
}

export function DocumentItem({ document, queryId, index }: DocumentItemProps) {
  const [showDetails, setShowDetails] = useState(false);
  const { isFavorite, toggleFavorite } = useFavorite(document.doc_id, queryId);

  const handleLinkClick = (e: React.MouseEvent<HTMLAnchorElement>) => {
    const url = new URL('/go/', window.location.origin);
    url.searchParams.set('docId', document.doc_id);
    url.searchParams.set('queryId', queryId);
    url.searchParams.set('order', String(index));
    url.searchParams.set('rt', String(Date.now()));

    e.currentTarget.href = url.toString();
  };

  return (
    <li className="document-item">
      <h3 className="title">
        <a
          href={document.url_link}
          onClick={handleLinkClick}
          className="link"
        >
          {document.content_title}
        </a>
      </h3>

      <div className="body">
        <Thumbnail
          docId={document.doc_id}
          queryId={queryId}
          alt={document.title}
        />

        <div
          className="description"
          dangerouslySetInnerHTML={{ __html: document.content_description }}
        />
      </div>

      <div className="site">
        <CopyButton text={document.url_link} />
        <cite>{document.site_path}</cite>
      </div>

      <button
        className="more-button"
        onClick={() => setShowDetails(!showDetails)}
      >
        More
      </button>

      {showDetails && (
        <div className="info">
          <time>{formatDate(document.last_modified || document.created)}</time>
          {document.content_length && (
            <span>{formatFileSize(document.content_length)}</span>
          )}
          {document.click_count && (
            <span>{document.click_count} views</span>
          )}
          {document.has_cache === 'true' && (
            <a href={`/cache/?docId=${document.doc_id}`}>Cache</a>
          )}
          {document.similar_docs_count > 1 && (
            <a href={buildSimilarDocsUrl(document)}>
              {document.similar_docs_count - 1} similar
            </a>
          )}
          <button onClick={toggleFavorite}>
            <i className={isFavorite ? 'fas fa-star' : 'far fa-star'} />
          </button>
        </div>
      )}
    </li>
  );
}
```

---

## 4. 状態管理設計

### 4.1 Zustandを使用した状態管理（推奨）

#### 4.1.1 検索状態ストア
```typescript
// store/searchStore.ts
import { create } from 'zustand';

interface SearchState {
  query: string;
  results: SearchResult | null;
  isLoading: boolean;
  error: Error | null;

  // Actions
  setQuery: (query: string) => void;
  executeSearch: (params: SearchFormData) => Promise<void>;
  clearResults: () => void;
}

export const useSearchStore = create<SearchState>((set, get) => ({
  query: '',
  results: null,
  isLoading: false,
  error: null,

  setQuery: (query) => set({ query }),

  executeSearch: async (params) => {
    set({ isLoading: true, error: null });
    try {
      const results = await searchDocuments(params);
      set({ results, isLoading: false });
    } catch (error) {
      set({ error, isLoading: false });
    }
  },

  clearResults: () => set({ results: null, error: null })
}));
```

#### 4.1.2 ユーザー状態ストア
```typescript
// store/userStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface UserState {
  user: UserInfo | null;
  isAuthenticated: boolean;

  // Actions
  setUser: (user: UserInfo) => void;
  logout: () => void;
}

export const useUserStore = create<UserState>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,

      setUser: (user) => set({ user, isAuthenticated: true }),
      logout: () => set({ user: null, isAuthenticated: false })
    }),
    {
      name: 'user-storage'
    }
  )
);
```

### 4.2 React Contextを使用した状態管理（代替案）

```typescript
// context/SearchContext.tsx
'use client';

import { createContext, useContext, useState, ReactNode } from 'react';

interface SearchContextType {
  query: string;
  setQuery: (query: string) => void;
  results: SearchResult | null;
  executeSearch: (params: SearchFormData) => Promise<void>;
}

const SearchContext = createContext<SearchContextType | undefined>(undefined);

export function SearchProvider({ children }: { children: ReactNode }) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<SearchResult | null>(null);

  const executeSearch = async (params: SearchFormData) => {
    const data = await searchDocuments(params);
    setResults(data);
  };

  return (
    <SearchContext.Provider value={{ query, setQuery, results, executeSearch }}>
      {children}
    </SearchContext.Provider>
  );
}

export function useSearch() {
  const context = useContext(SearchContext);
  if (!context) {
    throw new Error('useSearch must be used within SearchProvider');
  }
  return context;
}
```

---

## 5. API統合設計

### 5.1 APIクライアント設定

```typescript
// services/api/client.ts
import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_FESS_API_URL || 'http://localhost:8080/api/v1';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// リクエストインターセプター
apiClient.interceptors.request.use(
  (config) => {
    // 認証トークン追加（必要な場合）
    const token = getAuthToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// レスポンスインターセプター
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 認証エラー時の処理
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

### 5.2 検索APIサービス

```typescript
// services/api/search.ts
import { apiClient } from './client';
import type { SearchFormData, SearchResult } from '@/types/search';

export async function searchDocuments(params: SearchFormData): Promise<SearchResult> {
  const response = await apiClient.get<SearchResult>('/documents', { params });
  return response.data;
}

export async function getLabels(): Promise<LabelItem[]> {
  const response = await apiClient.get<{ data: LabelItem[] }>('/labels');
  return response.data.data;
}

export async function getPopularWords(params?: {
  seed?: string;
  label?: string[];
}): Promise<string[]> {
  const response = await apiClient.get<{ data: string[] }>('/popular-words', { params });
  return response.data.data;
}

export async function getSuggestWords(params: {
  q: string;
  num?: number;
  field?: string[];
  lang?: string[];
}): Promise<SuggestItem[]> {
  const response = await apiClient.get<{ data: SuggestItem[] }>('/suggest-words', { params });
  return response.data.data;
}

export async function getFavorites(queryId: string): Promise<string[]> {
  const response = await apiClient.get<{ data: { doc_id: string }[] }>('/favorites', {
    params: { queryId }
  });
  return response.data.data.map((item) => item.doc_id);
}

export async function setFavorite(docId: string, queryId: string): Promise<void> {
  await apiClient.post(`/documents/${docId}/favorite`, null, {
    params: { queryId }
  });
}
```

### 5.3 エラーハンドリング

```typescript
// services/api/errorHandler.ts
import type { AxiosError } from 'axios';

export interface ApiError {
  code: string;
  message: string;
  status?: number;
}

export function handleApiError(error: unknown): ApiError {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<{ error_code?: string; message?: string }>;

    return {
      code: axiosError.response?.data?.error_code || 'UNKNOWN_ERROR',
      message: axiosError.response?.data?.message || axiosError.message,
      status: axiosError.response?.status
    };
  }

  return {
    code: 'UNKNOWN_ERROR',
    message: error instanceof Error ? error.message : 'An unknown error occurred'
  };
}
```

---

## 6. カスタムフック設計

### 6.1 検索フック

```typescript
// hooks/useSearch.ts
import { useState, useCallback } from 'react';
import { searchDocuments } from '@/services/api/search';
import type { SearchFormData, SearchResult } from '@/types/search';

export function useSearch() {
  const [results, setResults] = useState<SearchResult | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const search = useCallback(async (params: SearchFormData) => {
    setIsLoading(true);
    setError(null);

    try {
      const data = await searchDocuments(params);
      setResults(data);
    } catch (err) {
      setError(err as Error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  return { results, isLoading, error, search };
}
```

### 6.2 サジェストフック

```typescript
// hooks/useSuggest.ts
import { useState, useEffect } from 'react';
import { useDebounce } from './useDebounce';
import { getSuggestWords } from '@/services/api/search';

export function useSuggest(query: string, delay: number = 300) {
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const debouncedQuery = useDebounce(query, delay);

  useEffect(() => {
    if (!debouncedQuery || debouncedQuery.length < 1) {
      setSuggestions([]);
      return;
    }

    let isCancelled = false;

    const fetchSuggestions = async () => {
      setIsLoading(true);
      try {
        const data = await getSuggestWords({
          q: debouncedQuery,
          field: ['_default', 'content', 'title'],
          num: 10
        });

        if (!isCancelled) {
          setSuggestions(data.map((item) => item.text));
        }
      } catch (error) {
        console.error('Failed to fetch suggestions:', error);
      } finally {
        if (!isCancelled) {
          setIsLoading(false);
        }
      }
    };

    fetchSuggestions();

    return () => {
      isCancelled = true;
    };
  }, [debouncedQuery]);

  return { suggestions, isLoading };
}
```

### 6.3 お気に入りフック

```typescript
// hooks/useFavorite.ts
import { useState, useEffect } from 'react';
import { getFavorites, setFavorite } from '@/services/api/search';

export function useFavorite(docId: string, queryId: string) {
  const [isFavorite, setIsFavorite] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const checkFavorite = async () => {
      try {
        const favorites = await getFavorites(queryId);
        setIsFavorite(favorites.includes(docId));
      } catch (error) {
        console.error('Failed to check favorite:', error);
      }
    };

    checkFavorite();
  }, [docId, queryId]);

  const toggleFavorite = async () => {
    if (isFavorite) return;

    setIsLoading(true);
    try {
      await setFavorite(docId, queryId);
      setIsFavorite(true);
    } catch (error) {
      console.error('Failed to set favorite:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return { isFavorite, isLoading, toggleFavorite };
}
```

### 6.4 デバウンスフック

```typescript
// hooks/useDebounce.ts
import { useState, useEffect } from 'react';

export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(timer);
    };
  }, [value, delay]);

  return debouncedValue;
}
```

---

## 7. ルーティング設計

### 7.1 App Routerのルート構成

```typescript
// app/layout.tsx
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { SearchProvider } from '@/context/SearchContext';
import { I18nProvider } from '@/context/I18nContext';

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <I18nProvider>
          <SearchProvider>
            <Header />
            {children}
            <Footer />
          </SearchProvider>
        </I18nProvider>
      </body>
    </html>
  );
}
```

### 7.2 ページ遷移とクエリパラメータ

```typescript
// utils/navigation.ts
import { useRouter, useSearchParams } from 'next/navigation';

export function useSearchNavigation() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const navigateToSearch = (params: SearchFormData) => {
    const query = new URLSearchParams();

    if (params.q) query.set('q', params.q);
    if (params.num) query.set('num', String(params.num));
    if (params.sort) query.set('sort', params.sort);
    if (params.lang) params.lang.forEach((l) => query.append('lang', l));
    if (params['fields.label']) {
      params['fields.label'].forEach((label) => query.append('fields.label', label));
    }
    if (params.ex_q) params.ex_q.forEach((eq) => query.append('ex_q', eq));

    router.push(`/search?${query.toString()}`);
  };

  const getCurrentParams = (): SearchFormData => {
    return {
      q: searchParams.get('q') || undefined,
      num: Number(searchParams.get('num')) || undefined,
      sort: searchParams.get('sort') || undefined,
      lang: searchParams.getAll('lang'),
      'fields.label': searchParams.getAll('fields.label'),
      ex_q: searchParams.getAll('ex_q')
    };
  };

  return { navigateToSearch, getCurrentParams };
}
```

---

## 8. スタイリング設計

### 8.1 Tailwind CSS設定

```typescript
// tailwind.config.ts
import type { Config } from 'tailwindcss';

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}'
  ],
  theme: {
    extend: {
      colors: {
        primary: '#0066cc',
        secondary: '#6c757d',
        success: '#28a745',
        danger: '#dc3545',
        warning: '#ffc107',
        info: '#17a2b8'
      },
      screens: {
        sm: '576px',
        md: '768px',
        lg: '992px',
        xl: '1200px',
        '2xl': '1400px'
      }
    }
  },
  plugins: []
};

export default config;
```

### 8.2 CSS Modules（代替案）

```css
/* components/search/SearchBox.module.css */
.searchBox {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.inputGroup {
  position: relative;
  flex: 1;
}

.searchInput {
  width: 100%;
  padding: 0.5rem 1rem;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
  font-size: 1rem;
}

.searchInput:focus {
  outline: none;
  border-color: #80bdff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.searchButton {
  padding: 0.5rem 1.5rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 0.25rem;
  cursor: pointer;
}

.searchButton:hover {
  background-color: #0056b3;
}

.searchButton:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
```

---

## 9. 国際化（i18n）設計

### 9.1 next-intl設定

```typescript
// i18n/config.ts
export const i18n = {
  locales: ['en', 'ja', 'ar', 'cs', 'de', 'es', 'fr', 'hu', 'id', 'it', 'ko', 'nl', 'pl', 'pt', 'ro', 'ru', 'th', 'tr', 'zh-CN', 'zh-TW'],
  defaultLocale: 'en'
};
```

```typescript
// middleware.ts
import createMiddleware from 'next-intl/middleware';
import { i18n } from './i18n/config';

export default createMiddleware({
  locales: i18n.locales,
  defaultLocale: i18n.defaultLocale
});

export const config = {
  matcher: ['/((?!api|_next|.*\\..*).*)']
};
```

### 9.2 言語ファイル構成

```json
// i18n/locales/en.json
{
  "search": {
    "title": "Search",
    "placeholder": "Enter search query",
    "button": "Search",
    "options": "Options",
    "advanced": "Advanced Search"
  },
  "results": {
    "status": "Showing {start} to {end} of {total} results for \"{query}\"",
    "status_over": "Showing {start} to {end} of over {total} results for \"{query}\"",
    "time": "({time} seconds)",
    "no_results": "No results found for \"{query}\"",
    "partial": "Search time exceeded. Results may be incomplete."
  },
  "sort": {
    "score": "Relevance",
    "filename_asc": "File Name (A-Z)",
    "filename_desc": "File Name (Z-A)",
    "created_asc": "Created Date (Oldest First)",
    "created_desc": "Created Date (Newest First)",
    "content_length_asc": "File Size (Smallest First)",
    "content_length_desc": "File Size (Largest First)",
    "last_modified_asc": "Last Modified (Oldest First)",
    "last_modified_desc": "Last Modified (Newest First)",
    "click_count_asc": "Click Count (Low to High)",
    "click_count_desc": "Click Count (High to Low)",
    "favorite_count_asc": "Favorite Count (Low to High)",
    "favorite_count_desc": "Favorite Count (High to Low)"
  }
}
```

---

## 10. テスト設計

### 10.1 単体テスト

```typescript
// __tests__/components/search/SearchBox.test.tsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { SearchBox } from '@/components/search/SearchBox';

describe('SearchBox', () => {
  it('renders search input', () => {
    render(<SearchBox />);
    expect(screen.getByRole('textbox')).toBeInTheDocument();
  });

  it('calls onSubmit with query when submitted', async () => {
    const handleSubmit = jest.fn();
    render(<SearchBox onSubmit={handleSubmit} />);

    const input = screen.getByRole('textbox');
    fireEvent.change(input, { target: { value: 'test query' } });

    const button = screen.getByRole('button', { name: /search/i });
    fireEvent.click(button);

    await waitFor(() => {
      expect(handleSubmit).toHaveBeenCalledWith('test query');
    });
  });

  it('shows suggestions when typing', async () => {
    const { container } = render(<SearchBox showSuggest />);

    const input = screen.getByRole('textbox');
    fireEvent.change(input, { target: { value: 'fess' } });

    await waitFor(() => {
      expect(container.querySelector('.suggest-list')).toBeInTheDocument();
    });
  });
});
```

### 10.2 E2Eテスト

```typescript
// e2e/search.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Search Flow', () => {
  test('should perform basic search', async ({ page }) => {
    await page.goto('/');

    await page.fill('input[type="text"]', 'fess');
    await page.click('button:has-text("Search")');

    await expect(page).toHaveURL(/\/search\?q=fess/);
    await expect(page.locator('.document-item')).toHaveCount.greaterThan(0);
  });

  test('should navigate through pagination', async ({ page }) => {
    await page.goto('/search?q=test');

    await page.click('a:has-text("2")');
    await expect(page).toHaveURL(/pn=2/);
  });

  test('should filter by facet', async ({ page }) => {
    await page.goto('/search?q=test');

    await page.click('.facet-item:first-child a');
    await expect(page).toHaveURL(/ex_q=/);
  });
});
```

---

## 11. パフォーマンス最適化

### 11.1 画像最適化

```typescript
// components/search/Thumbnail.tsx
import Image from 'next/image';

export function Thumbnail({ docId, queryId, alt }) {
  return (
    <Image
      src={`/thumbnail/?docId=${docId}&queryId=${queryId}`}
      alt={alt}
      width={150}
      height={150}
      loading="lazy"
      placeholder="blur"
      blurDataURL="/images/blank.png"
    />
  );
}
```

### 11.2 コード分割

```typescript
// Dynamic imports for heavy components
import dynamic from 'next/dynamic';

const AdvancedSearchForm = dynamic(() => import('@/components/advance/AdvancedSearchForm'), {
  loading: () => <div>Loading...</div>,
  ssr: false
});
```

### 11.3 キャッシング戦略

```typescript
// app/search/page.tsx
export const revalidate = 60; // ISR: 60秒ごとに再生成

// APIキャッシング
const results = await fetch('/api/v1/documents', {
  next: { revalidate: 60 }
});
```

---

## 12. セキュリティ考慮事項

### 12.1 XSS対策

```typescript
// dangerouslySetInnerHTMLの使用を最小限に
// 必要な場合はDOMPurifyでサニタイズ
import DOMPurify from 'isomorphic-dompurify';

function SafeHTML({ html }: { html: string }) {
  const clean = DOMPurify.sanitize(html);
  return <div dangerouslySetInnerHTML={{ __html: clean }} />;
}
```

### 12.2 環境変数管理

```bash
# .env.local
NEXT_PUBLIC_FESS_API_URL=http://localhost:8080/api/v1
FESS_API_KEY=secret_key_here
```

```typescript
// 環境変数のバリデーション
const env = {
  apiUrl: process.env.NEXT_PUBLIC_FESS_API_URL,
  apiKey: process.env.FESS_API_KEY
};

if (!env.apiUrl) {
  throw new Error('NEXT_PUBLIC_FESS_API_URL is not defined');
}
```

---

## 13. デプロイメント設計

### 13.1 ビルド設定

```javascript
// next.config.js
/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'standalone',
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'your-fess-server.com'
      }
    ]
  },
  async rewrites() {
    return [
      {
        source: '/api/v1/:path*',
        destination: `${process.env.FESS_API_URL}/:path*`
      }
    ];
  }
};

module.exports = nextConfig;
```

### 13.2 Docker設定

```dockerfile
# Dockerfile
FROM node:20-alpine AS base

# Dependencies
FROM base AS deps
WORKDIR /app
COPY package*.json ./
RUN npm ci

# Builder
FROM base AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

# Runner
FROM base AS runner
WORKDIR /app
ENV NODE_ENV production

COPY --from=builder /app/public ./public
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static

EXPOSE 3000
CMD ["node", "server.js"]
```

---

## 14. 監視・ロギング

### 14.1 エラー監視（Sentry）

```typescript
// sentry.config.ts
import * as Sentry from '@sentry/nextjs';

Sentry.init({
  dsn: process.env.NEXT_PUBLIC_SENTRY_DSN,
  tracesSampleRate: 1.0
});
```

### 14.2 アナリティクス

```typescript
// lib/analytics.ts
export function trackSearchEvent(query: string, resultCount: number) {
  if (typeof window !== 'undefined' && window.gtag) {
    window.gtag('event', 'search', {
      search_term: query,
      result_count: resultCount
    });
  }
}
```

---

## 15. 開発ワークフロー

### 15.1 開発環境セットアップ

```bash
# 依存関係インストール
npm install

# 開発サーバー起動
npm run dev

# テスト実行
npm run test

# E2Eテスト
npm run test:e2e

# ビルド
npm run build

# 本番環境起動
npm run start
```

### 15.2 コード品質チェック

```json
// package.json
{
  "scripts": {
    "lint": "next lint",
    "format": "prettier --write \"src/**/*.{ts,tsx}\"",
    "type-check": "tsc --noEmit"
  }
}
```

---

## 16. まとめ

本設計書では、Fessの検索画面をNext.js + TypeScriptで実装するための詳細な技術設計を示しました。

### 主要な設計ポイント:
1. **Next.js App Routerの採用**: 最新のNext.js機能を活用
2. **TypeScriptによる型安全性**: すべてのコンポーネント・関数に型定義
3. **Zustandによる状態管理**: シンプルで効率的な状態管理
4. **Fess OpenAPI連携**: 既存APIを活用した実装
5. **レスポンシブデザイン**: Bootstrap互換のブレークポイント
6. **国際化対応**: 17言語サポート
7. **パフォーマンス最適化**: 画像遅延読み込み、コード分割
8. **テスト自動化**: 単体テスト・E2Eテストの実装
9. **セキュリティ対策**: XSS対策、環境変数管理
10. **運用監視**: エラー監視、アナリティクス

この設計に基づいて実装することで、Fessの検索機能を完全に再現しつつ、モダンなフロントエンド技術スタックを活用した高品質なアプリケーションを構築できます。
