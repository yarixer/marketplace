export { matchers } from './matchers.js';

export const nodes = [
	() => import('./nodes/0'),
	() => import('./nodes/1'),
	() => import('./nodes/2'),
	() => import('./nodes/3'),
	() => import('./nodes/4'),
	() => import('./nodes/5'),
	() => import('./nodes/6'),
	() => import('./nodes/7'),
	() => import('./nodes/8'),
	() => import('./nodes/9'),
	() => import('./nodes/10'),
	() => import('./nodes/11'),
	() => import('./nodes/12'),
	() => import('./nodes/13'),
	() => import('./nodes/14'),
	() => import('./nodes/15'),
	() => import('./nodes/16'),
	() => import('./nodes/17'),
	() => import('./nodes/18'),
	() => import('./nodes/19'),
	() => import('./nodes/20'),
	() => import('./nodes/21'),
	() => import('./nodes/22'),
	() => import('./nodes/23'),
	() => import('./nodes/24'),
	() => import('./nodes/25'),
	() => import('./nodes/26'),
	() => import('./nodes/27'),
	() => import('./nodes/28'),
	() => import('./nodes/29')
];

export const server_loads = [0,2,3,4];

export const dictionary = {
		"/": [~5],
		"/account": [~6,[2]],
		"/account/general": [~7,[2]],
		"/account/library": [~8,[2]],
		"/account/orders": [~9,[2]],
		"/account/purchase-history": [~10,[2]],
		"/account/security": [~11,[2]],
		"/admin": [~12,[3]],
		"/admin/moderation": [~13,[3]],
		"/admin/moderation/[revisionId]": [~14,[3]],
		"/admin/products": [~15,[3]],
		"/admin/products/[productId]": [~16,[3]],
		"/admin/users": [~17,[3]],
		"/admin/users/[userId]": [~18,[3]],
		"/admin/wallet": [~19,[3]],
		"/auth/login": [~20],
		"/auth/register": [~21],
		"/products": [~22],
		"/products/[slug]": [~23],
		"/seller/dashboard": [~24,[4]],
		"/seller/products": [~25,[4]],
		"/seller/products/new": [~26,[4]],
		"/seller/products/[productId]": [~27,[4]],
		"/seller/settings": [28,[4]],
		"/seller/stats": [~29,[4]]
	};

export const hooks = {
	handleError: (({ error }) => { console.error(error) }),
	
	reroute: (() => {}),
	transport: {}
};

export const decoders = Object.fromEntries(Object.entries(hooks.transport).map(([k, v]) => [k, v.decode]));
export const encoders = Object.fromEntries(Object.entries(hooks.transport).map(([k, v]) => [k, v.encode]));

export const hash = false;

export const decode = (type, value) => decoders[type](value);

export { default as root } from '../root.js';