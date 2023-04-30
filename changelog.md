## **(NOTE: Delete the Mass Fortify config file to update mana costs properly. Find in config > amethyst_imbuement > augments > mass_fortify_v2.json)**

### Fixes
* Fix innate abilities of imbued trinkets not stacking properly if 2+ trinkets use the same innate ability. (Items crafted before this update will still have this problem unfortunately)
* Fix Fortify missing maximum status effect cap
* Fixed Spiked not applying to all shields.
* Fixed missing Insight Potion recipes
* Fixed missing Mental Clarity recipe

### Additions
* Added a reroll button to the enchanting mode of the imbuing table. It is modeled after Easy Magic's reroll mechanic. Enabled by default in configs.

### Changes
* Bulwark is now an augment, not a standard enchantment. An imbuing recipe for it has been added.
* Mana cost of Mass Fortify updated (**need augment config reset**). 
* Hard Light Blocks placed directly by a player will now drop themselves when broken. Spell-placed blocks will vanish.