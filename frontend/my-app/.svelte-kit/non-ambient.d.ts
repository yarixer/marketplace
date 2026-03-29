
// this file is generated — do not edit it


declare module "svelte/elements" {
	export interface HTMLAttributes<T> {
		'data-sveltekit-keepfocus'?: true | '' | 'off' | undefined | null;
		'data-sveltekit-noscroll'?: true | '' | 'off' | undefined | null;
		'data-sveltekit-preload-code'?:
			| true
			| ''
			| 'eager'
			| 'viewport'
			| 'hover'
			| 'tap'
			| 'off'
			| undefined
			| null;
		'data-sveltekit-preload-data'?: true | '' | 'hover' | 'tap' | 'off' | undefined | null;
		'data-sveltekit-reload'?: true | '' | 'off' | undefined | null;
		'data-sveltekit-replacestate'?: true | '' | 'off' | undefined | null;
	}
}

export {};


declare module "$app/types" {
	type MatcherParam<M> = M extends (param : string) => param is (infer U extends string) ? U : string;

	export interface AppTypes {
		RouteId(): "/" | "/account" | "/account/general" | "/account/library" | "/account/library/download" | "/account/library/download/[productId]" | "/account/orders" | "/account/purchase-history" | "/account/security" | "/admin" | "/admin/moderation" | "/admin/moderation/[revisionId]" | "/admin/products" | "/admin/products/[productId]" | "/admin/users" | "/admin/users/[userId]" | "/admin/wallet" | "/auth" | "/auth/login" | "/auth/logout" | "/auth/register" | "/products" | "/products/[slug]" | "/seller" | "/seller/dashboard" | "/seller/products" | "/seller/products/new" | "/seller/products/[productId]" | "/seller/settings" | "/seller/stats";
		RouteParams(): {
			"/account/library/download/[productId]": { productId: string };
			"/admin/moderation/[revisionId]": { revisionId: string };
			"/admin/products/[productId]": { productId: string };
			"/admin/users/[userId]": { userId: string };
			"/products/[slug]": { slug: string };
			"/seller/products/[productId]": { productId: string }
		};
		LayoutParams(): {
			"/": { productId?: string; revisionId?: string; userId?: string; slug?: string };
			"/account": { productId?: string };
			"/account/general": Record<string, never>;
			"/account/library": { productId?: string };
			"/account/library/download": { productId?: string };
			"/account/library/download/[productId]": { productId: string };
			"/account/orders": Record<string, never>;
			"/account/purchase-history": Record<string, never>;
			"/account/security": Record<string, never>;
			"/admin": { revisionId?: string; productId?: string; userId?: string };
			"/admin/moderation": { revisionId?: string };
			"/admin/moderation/[revisionId]": { revisionId: string };
			"/admin/products": { productId?: string };
			"/admin/products/[productId]": { productId: string };
			"/admin/users": { userId?: string };
			"/admin/users/[userId]": { userId: string };
			"/admin/wallet": Record<string, never>;
			"/auth": Record<string, never>;
			"/auth/login": Record<string, never>;
			"/auth/logout": Record<string, never>;
			"/auth/register": Record<string, never>;
			"/products": { slug?: string };
			"/products/[slug]": { slug: string };
			"/seller": { productId?: string };
			"/seller/dashboard": Record<string, never>;
			"/seller/products": { productId?: string };
			"/seller/products/new": Record<string, never>;
			"/seller/products/[productId]": { productId: string };
			"/seller/settings": Record<string, never>;
			"/seller/stats": Record<string, never>
		};
		Pathname(): "/" | "/account" | "/account/general" | "/account/library" | `/account/library/download/${string}` & {} | "/account/orders" | "/account/purchase-history" | "/account/security" | "/admin" | "/admin/moderation" | `/admin/moderation/${string}` & {} | "/admin/products" | `/admin/products/${string}` & {} | "/admin/users" | `/admin/users/${string}` & {} | "/admin/wallet" | "/auth/login" | "/auth/logout" | "/auth/register" | "/products" | `/products/${string}` & {} | "/seller/dashboard" | "/seller/products" | "/seller/products/new" | `/seller/products/${string}` & {} | "/seller/settings" | "/seller/stats";
		ResolvedPathname(): `${"" | `/${string}`}${ReturnType<AppTypes['Pathname']>}`;
		Asset(): "/diamond.gif" | "/favicon.ico" | "/logo.webp" | "/robots.txt" | string & {};
	}
}