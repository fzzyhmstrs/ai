### Additions
* Added config options for experience bush growth chance and bonemeal chance in the Altar_v3 config file
* Added integration with Enchanting Table Descriptions; the imbuing table tooltips will now show the enchantment and augment descriptions in the same manner as Enchantment Descriptions.
* Added alternate gem dust recipes for Citrine, Smoky Quartz, and Danburite
* zh_cn localization added.

### Changes
* **Now powered by Fzzy Core and the refactored Amethyst Core. These libraries are part of a project to divide the previous Amethyst Core into more streamlined and focused parts. This does not change any functionality of AI, but it will now require downloading AC and FC as dependencies. I will no longer be embedding them.**
* Mana potions now repair slightly more.
* Mana potions throw like experience bottles if there is nothing to heal rather than having to drink them for XP.
* Increased the drinking speed of mana potions.
* Scepter modifiers have a new layout now, with descriptions (if advanced tooltips are active)
* Spell types now utilize a tag system, opening the possibility for creators to change what spells are defined as what.

### Fixes
* Mana potions now repair items in the offhand and armor slots
* Imbuing tables now drop their inventory when broken
* EMI can now transfer augment recipes into the imbuing table properly.
* Two hidden advancements now properly have loot tables.