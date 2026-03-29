<script lang="ts">
	let { diamondSrc = '/diamond.gif' }: { diamondSrc?: string } = $props();
</script>

<section class="py-6 md:py-10">
	<div class="mx-auto max-w-5xl">
		<div class="flex flex-col items-center text-center">

			<p class="mt-4 text-sm font-semibold uppercase tracking-[0.24em] text-[#ff4646] md:text-base">
				More than just a webpage...
			</p>

			<div class="mt-10 flex justify-center">
				<div class="relative flex items-center justify-center">
					<div class="absolute inset-0 scale-125 rounded-full bg-[radial-gradient(circle,rgba(255,70,70,0.08),transparent_65%)] blur-2xl"></div>

					<img
						src={diamondSrc}
						alt="Diamond animation"
						class="relative h-72 w-72 object-contain md:h-[26rem] md:w-[26rem]"
					/>
				</div>
			</div>
		</div>
	</div>
</section>