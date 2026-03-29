<script lang="ts">
	let {
		pathname
	}: {
		pathname: string;
	} = $props();

	const links = [
		{ href: '/admin/moderation', label: 'Moderation' },
		{ href: '/admin/users', label: 'Users' },
		{ href: '/admin/products', label: 'Products' },
		{ href: '/admin/wallet', label: 'Wallet Tools' }
	];

	function isActive(href: string) {
		return pathname.startsWith(href);
	}
</script>

<div class="flex flex-wrap items-center gap-3 px-5 py-5 md:px-8">
	{#each links as link}
		<a
			href={link.href}
			class={`px-4 py-2 text-sm font-bold transition ${
				isActive(link.href)
					? 'rounded-[8px] bg-[#ff4646] text-white'
					: 'text-[#32435a] hover:text-black'
			}`}
		>
			{link.label}
		</a>
	{/each}
</div>