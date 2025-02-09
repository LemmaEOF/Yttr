package com.unascribed.yttr.compat.rei;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.RuinedRecipeResourceMetadata;
import com.unascribed.yttr.content.item.DropOfContinuityItem;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.crafting.CentrifugingRecipe;
import com.unascribed.yttr.crafting.LampRecipe;
import com.unascribed.yttr.crafting.PistonSmashingRecipe;
import com.unascribed.yttr.crafting.SoakingRecipe;
import com.unascribed.yttr.crafting.VoidFilteringRecipe;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YRecipeTypes;
import com.unascribed.yttr.mechanics.LampColor;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.EntryStack.Settings;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.DefaultPlugin;
import me.shedaniel.rei.plugin.crafting.DefaultCraftingDisplay;
import me.shedaniel.rei.plugin.crafting.DefaultCustomDisplay;
import me.shedaniel.rei.plugin.stripping.DefaultStrippingDisplay;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public class YttrREIPlugin implements REIPluginV0 {

	public static final Identifier ID = new Identifier("yttr", "main");
	public static final VoidFilteringCategory VOID_FILTERING = new VoidFilteringCategory();
	public static final PistonSmashingCategory PISTON_SMASHING = new PistonSmashingCategory();
	public static final CentrifugingCategory CENTRIFUGING = new CentrifugingCategory();
	public static final SoakingCategory SOAKING = new SoakingCategory();
	public static final ContinuityCategory CONTINUITY = new ContinuityCategory();
	public static final RuinedCategory RUINED = new RuinedCategory();
	public static final LampCraftingCategory LAMP_CRAFTING = new LampCraftingCategory();
	
	@Override
	public void registerPluginCategories(RecipeHelper recipeHelper) {
		recipeHelper.registerCategory(VOID_FILTERING);
		recipeHelper.registerCategory(PISTON_SMASHING);
		recipeHelper.registerCategory(CENTRIFUGING);
		recipeHelper.registerCategory(SOAKING);
		recipeHelper.registerCategory(CONTINUITY);
		recipeHelper.registerCategory(RUINED);
		recipeHelper.registerCategory(LAMP_CRAFTING);
	}
	
	@Override
	public void registerOthers(RecipeHelper recipeHelper) {
		recipeHelper.registerWorkingStations(VoidFilteringCategory.ID, EntryStack.create(YItems.VOID_FILTER));
		recipeHelper.registerWorkingStations(PistonSmashingCategory.ID, EntryStack.create(Blocks.PISTON));
		recipeHelper.registerWorkingStations(PistonSmashingCategory.ID, EntryStack.create(Blocks.STICKY_PISTON));
		recipeHelper.registerWorkingStations(CentrifugingCategory.ID, EntryStack.create(YBlocks.CENTRIFUGE));
		recipeHelper.registerWorkingStations(ContinuityCategory.ID, EntryStack.create(YItems.DROP_OF_CONTINUITY));
		recipeHelper.registerWorkingStations(ContinuityCategory.ID, EntryStack.create(YItems.LOOTBOX_OF_CONTINUITY));
	}
	
	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper) {
		List<VoidFilteringRecipe> sorted = Lists.newArrayList(recipeHelper.getRecipeManager().listAllOfType(YRecipeTypes.VOID_FILTERING));
		sorted.sort((a, b) -> Double.compare(b.getChance(), a.getChance()));
		for (VoidFilteringRecipe r : sorted) {
			if (r.isHidden()) continue;
			recipeHelper.registerDisplay(new VoidFilteringEntry(r.getId(), EntryStack.create(r.getOutput()), r.getChance()));
		}
		for (PistonSmashingRecipe r : recipeHelper.getRecipeManager().listAllOfType(YRecipeTypes.PISTON_SMASHING)) {
			ItemStack multCloudOutput = r.getCloudOutput().copy();
			multCloudOutput.setCount(multCloudOutput.getCount()*r.getCloudSize());
			recipeHelper.registerDisplay(new PistonSmashingEntry(r.getId(), r.getInput().getMatchingBlocks(), r.getCatalyst().getMatchingBlocks(), EntryStack.create(r.getOutput()),
					r.getCloudColor(), EntryStack.create(multCloudOutput)));
		}
		for (CentrifugingRecipe r : recipeHelper.getRecipeManager().listAllOfType(YRecipeTypes.CENTRIFUGING)) {
			List<ItemStack> inputs = Lists.newArrayList(Lists.transform(Arrays.asList(r.getInput().getMatchingStacksClient()), (is) -> {
				is = is.copy();
				is.setCount(r.getInputCount());
				return is;
			}));
			recipeHelper.registerDisplay(new CentrifugingEntry(r.getId(), EntryStack.ofItemStacks(inputs), EntryStack.ofItemStacks(r.getOutputs())));
		}
		for (SoakingRecipe r : recipeHelper.getRecipeManager().listAllOfType(YRecipeTypes.SOAKING)) {
			recipeHelper.registerDisplay(new SoakingEntry(r.getId(),
					EntryStack.ofIngredients(r.getIngredients()),
					r.getCatalyst().getMatchingFluids().stream()
						.map(EntryStack::create)
						.collect(Collectors.toList()),
					ImmutableList.of(EntryStack.create(r.getOutput())),
					r.getResult().right().isPresent()
				));
		}
		recipeHelper.registerDisplay(new ContinuityEntry(Collections2.transform(DropOfContinuityItem.getPossibilities(), ItemStack::new)));
		ResourceManager rm = MinecraftClient.getInstance().getResourceManager();
		for (Identifier id : rm.findResources("textures/gui/ruined_recipe", path -> path.endsWith(".png"))) {
			String name = id.getPath();
			name = name.substring(27, name.length()-4);
			if (id.getNamespace().equals("yttr") && (name.equals("border") || name.equals("overlay"))) continue;
			Identifier itemId = new Identifier(id.getNamespace(), name);
			Item result = Registry.ITEM.getOrEmpty(itemId).orElse(null);
			if (result != null) {
				RuinedRecipeResourceMetadata meta = null;
				try {
					meta = rm.getResource(id).getMetadata(RuinedRecipeResourceMetadata.READER);
				} catch (IOException e) {
				}
				Set<Integer> emptySlots = Collections.emptySet();
				if (meta != null) {
					emptySlots = meta.getEmptySlots();
				}
				recipeHelper.registerDisplay(new RuinedEntry(itemId, EntryStack.create(result), emptySlots));
			}
		}
		recipeHelper.registerDisplay(new DefaultStrippingDisplay(new ItemStack(YBlocks.SQUEEZE_LOG), new ItemStack(YBlocks.STRIPPED_SQUEEZE_LOG)));
		
		recipeHelper.registerRecipeVisibilityHandler((cat, recipe) -> {
			// hide REI's built-in lamp recipe displays since they're busted
			if (recipe instanceof DefaultCraftingDisplay && !(recipe instanceof DefaultCustomDisplay) && ((DefaultCraftingDisplay)recipe).getOptionalRecipe().map(r -> r instanceof LampRecipe).orElse(false)) {
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});
		
		Hash.Strategy<ItemStack> itemStackStrategy = new Strategy<ItemStack>() {

			@Override
			public int hashCode(ItemStack o) {
				return Objects.hash(o.getItem(), o.getCount(), o.getTag());
			}

			@Override
			public boolean equals(ItemStack a, ItemStack b) {
				if (a == b) return true;
				if (a == null) return false;
				if (b == null) return false;
				return ItemStack.areItemsEqual(a, b) && ItemStack.areTagsEqual(a, b) && a.getCount() == b.getCount();
			}
			
		};
		Multimap<ItemStack, List<ItemStack>> resultsToInputs = Multimaps.newMultimap(new Object2ObjectLinkedOpenCustomHashMap<>(itemStackStrategy), Lists::newArrayList);
		
		for (Recipe<?> r : recipeHelper.getAllSortedRecipes()) {
			if (r instanceof LampRecipe) {
				resultsToInputs.clear();
				LampRecipe lr = (LampRecipe)r;
				DefaultedList<Ingredient> ingredients = lr.getIngredients();
				boolean interesting = false;
				int w = 0;
				int h = 0;
				sizeCheck: for (int i = 1; i <= 3; i++) {
					for (int j = 1; j <= 3; j++) {
						if (lr.fits(i, j)) {
							w = i;
							h = j;
							break sizeCheck;
						}
					}
				}
				CraftingInventory inv = new CraftingInventory(new ScreenHandler(null, 0) {
					@Override
					public boolean canUse(PlayerEntity player) {
						return false;
					}
				}, w, h);
				for (int i = 0; i < ingredients.size(); i++) {
					ItemStack[] matching = ingredients.get(i).getMatchingStacksClient();
					if (matching.length == 0) {
						inv.setStack(i, ItemStack.EMPTY);
					} else {
						inv.setStack(i, matching[0]);
					}
				}
				if (interesting) {
					System.out.println(r.getId()+" is "+w+"x"+h);
					System.out.println("initial inventory setup: "+Yttr.asList(inv));
				}
				final boolean finteresting = interesting;
				// go through every possible permutation of the inputs and figure out what causes different outputs
				for (int i = 0; i < ingredients.size(); i++) {
					final int fi = i;
					for (ItemStack is : ingredients.get(i).getMatchingStacksClient()) {
						permute(is, isp -> {
							inv.setStack(fi, isp);
							for (int j = 0; j < ingredients.size(); j++) {
								if (ingredients.size() > 1 && j == fi) continue;
								final int fj = j;
								for (ItemStack is2 : ingredients.get(j).getMatchingStacksClient()) {
									permute(is2, isp2 -> {
										inv.setStack(fj, isp2);
										ItemStack result = lr.craft(inv);
										if (finteresting) {
											System.out.println(Yttr.asList(inv)+" => "+result);
										}
										if (!result.isEmpty()) {
											resultsToInputs.put(result, Lists.newArrayList(Yttr.asList(inv)));
										}
									});
								}
							}
						});
					}
				}
				// now construct recipe displays for every unique output
				for (int pass = 0; pass < 3; pass++) {
					for (Map.Entry<ItemStack, Collection<List<ItemStack>>> en : resultsToInputs.asMap().entrySet()) {
						int itemCount = en.getValue().iterator().next().size();
						int desiredPass;
						if (en.getKey().getCount() > 1 || itemCount == 1) {
							// register multioutput and single-input recipes first so they show first, as they're "entrance" recipes
							desiredPass = 0;
						} else if (itemCount > 2) {
							// register "big" recipes after that, as they're probably "recombine" recipes
							desiredPass = 1;
						} else {
							// the rest are dye and invert recipes, which can trail at the end
							desiredPass = 2;
						}
						if (pass != desiredPass) continue;
						List<Set<ItemStack>> fin = Lists.newArrayList();
						for (List<ItemStack> inputs : en.getValue()) {
							for (int i = 0; i < inputs.size(); i++) {
								if (fin.size() <= i) {
									fin.add(new ObjectLinkedOpenCustomHashSet<>(itemStackStrategy));
								}
								fin.get(i).add(inputs.get(i));
							}
						}
						ItemStack result = en.getKey();
						List<Text> tip = Lists.newArrayList();
						if (result.getItem() == YItems.SUIT_HELMET) {
							LampColor color = LampBlockItem.getColor(result);
							tip.add(new TranslatableText("rei.yttr.suit_helmet_hud", new TranslatableText("color.yttr."+color.asString())
									.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color.baseLitColor)))));
						}
						DCDCons cons;
						if (desiredPass != 0) {
							cons = LampDisplay::new;
						} else {
							cons = DefaultCustomDisplay::new;
						}
						DefaultCustomDisplay disp = cons.construct(lr,
								Lists.transform(fin, c -> Lists.newArrayList(Iterables.transform(c, is -> EntryStack.create(is).setting(Settings.CHECK_TAGS, Settings.TRUE)))),
								Collections.singletonList(EntryStack.create(result)
										.setting(Settings.CHECK_TAGS, (desiredPass != 0 || itemCount == 1) && result.getItem() instanceof LampBlockItem ? Settings.TRUE : Settings.FALSE)
										.setting(Settings.TOOLTIP_APPEND_EXTRA, (es) -> tip)));
						recipeHelper.registerDisplay(disp);
					}
				}
			}
		}
	}
	
	private interface DCDCons { DefaultCustomDisplay construct(Recipe<?> possibleRecipe, List<List<EntryStack>> input, List<EntryStack> output); }
	
	private void permute(ItemStack is, Consumer<ItemStack> cb) {
		cb.accept(is);
		if (is.getItem() instanceof LampBlockItem) {
			for (LampColor lc : LampColor.VALUES) {
				ItemStack is2 = is.copy();
				LampBlockItem.setColor(is2, lc);
				LampBlockItem.setInverted(is2, false);
				cb.accept(is2);
				is2 = is2.copy();
				LampBlockItem.setInverted(is2, true);
				cb.accept(is2);
			}
		}
	}

	@Override
	public void registerEntries(EntryRegistry entryRegistry) {
		entryRegistry.removeEntry(EntryStack.create(YItems.LOGO));
		entryRegistry.removeEntry(EntryStack.create(YItems.LOOTBOX_OF_CONTINUITY));
	}
	
	@Override
	public void postRegister() {
		RecipeHelper recipeHelper = RecipeHelper.getInstance();
		recipeHelper.registerWorkingStations(LampCraftingCategory.ID, recipeHelper.getWorkingStations(DefaultPlugin.CRAFTING).toArray(new List[0]));
	}
	
	@Override
	public Identifier getPluginIdentifier() {
		return ID;
	}

}
