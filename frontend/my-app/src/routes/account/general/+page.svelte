<script lang="ts">
	import type { ActionData, PageData } from './$types';

	let { data, form }: { data: PageData; form: ActionData } = $props();
</script>

<div class="space-y-14">
	<section class="space-y-5">
		<div>
			<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Profile</h2>
		</div>

		<form method="POST" action="?/updateProfile" class="space-y-6">
			<div class="grid gap-5 md:grid-cols-2">
				<div class="space-y-2">
					<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="displayName">
						Display Name
					</label>
					<input
						id="displayName"
						name="displayName"
						type="text"
						required
						value={form?.profileValues?.displayName ?? data.user.displayName}
						class="w-full rounded-[6px] border border-neutral-400 px-4 py-3 text-[15px] outline-none transition focus:border-black"
					/>
				</div>

				<div class="space-y-2">
					<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="email">
						Email Address
					</label>
					<input
						id="email"
						type="email"
						value={data.user.email}
						readonly
						class="w-full rounded-[6px] border border-neutral-300 bg-neutral-100 px-4 py-3 text-[15px] text-neutral-500 outline-none"
					/>
				</div>
			</div>

			{#if form?.profileMessage}
				<p
					class={`px-4 py-3 text-sm ${
						form.profileSuccess
							? 'bg-green-50 text-green-700'
							: 'bg-red-50 text-red-700'
					}`}
				>
					{form.profileMessage}
				</p>
			{/if}

			<div class="flex justify-end">
				<button
					type="submit"
					class="rounded-[6px] bg-[#ff4646] px-6 py-3 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
				>
					Save Changes
				</button>
			</div>
		</form>
	</section>

	<section class="space-y-5">
		<div>
			<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Seller</h2>
			<p class="mt-2 max-w-3xl text-sm leading-6 text-neutral-600">
				Manage your seller access from here.
			</p>
		</div>

		{#if data.user.seller}
			<div class="space-y-4">
				<p class="text-sm text-neutral-700">
					You already have seller access as
					<strong>{data.user.sellerProfile?.publicName}</strong>.
				</p>

				<a
					href="/seller/dashboard"
					class="inline-flex rounded-[6px] bg-amber-300 px-6 py-3 text-sm font-bold text-black transition hover:bg-amber-400"
				>
					Open Seller Panel
				</a>
			</div>
		{:else}
			<form method="POST" action="?/becomeSeller" class="max-w-2xl space-y-5">
				<div class="space-y-2">
					<label class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400" for="publicName">
						Seller Public Name
					</label>
					<input
						id="publicName"
						name="publicName"
						type="text"
						required
						value={form?.sellerValues?.publicName ?? data.user.displayName}
						class="w-full rounded-[6px] border border-neutral-400 px-4 py-3 text-[15px] outline-none transition focus:border-black"
					/>
				</div>

				{#if form?.sellerMessage}
					<p
						class={`px-4 py-3 text-sm ${
							form.sellerSuccess
								? 'bg-green-50 text-green-700'
								: 'bg-red-50 text-red-700'
						}`}
					>
						{form.sellerMessage}
					</p>
				{/if}

				<div class="flex justify-end">
					<button
						type="submit"
						class="rounded-[6px] bg-[#ff4646] px-6 py-3 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
					>
						Become Seller
					</button>
				</div>
			</form>
		{/if}
	</section>
</div>