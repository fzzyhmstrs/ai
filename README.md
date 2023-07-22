# Amethyst Imbuement
<p align="left">
<a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-brightgreen.svg"></a>
</p>

[https://modrinth.com/mod/amethyst-imbuement](https://modrinth.com/mod/amethyst-imbuement)

[https://www.curseforge.com/minecraft/mc-mods/amethyst-imbuement](https://www.curseforge.com/minecraft/mc-mods/amethyst-imbuement)

This is a mod focused on magic and enchanting. 
Create magical crystals with various properties. 
Imbue items with powerful new abilities 
Channel mana through scepters to unleash devastation upon your enemies, bolster you and your allies, and summon creatures.

### Spell Update Progress 7/73

/*
Checklist
- canTarget if entity spell
- Build description for
  - Unique combinations
  - stat modifications
  - other type interactions
- add Lang
- provideArgs
- spells are equal check
- special names for uniques
- onPaired to grant relevant adv.
- implement all special combinations
- fill up interaction methods
  - onEntityHit?
  - onEntityKill?
  - onBlockHit?
  - Remember to call and check results of the super for the "default" behavior
- modify stats. don't forget mana cost and cooldown!
- modifyDealtDamage for unique interactions
- modifyDamageSource?
- remember DamageSourceBuilder for a default damage source
- modify other things
  - summon?
  - projectile?
  - explosion?
  - drops?
  - count? (affects some things like summon count and projectile count)
- sound and particles
*/

### TODO List
| Task                                                                         | Status      |
|------------------------------------------------------------------------------|-------------|
| NOT STARTED                                                                  | 13          |
| Add sounds for Ice Shard and HailStorm                                       | Not started |
| Lightning, Fang, Ball Lightning entities need fallbacks for empty spell      | Not started |
| Add secondary effects to fallback for fireball/wither skull                  | Not started |
| Update canTarget methods to account for other owned entities                 | Not started |
| Add sounds for the Floral Construct                                          | Not started |
| check on bulwark and maybe make it an augment                                | Not started |
| Impl combination modification into augment configs                           | Not started |
| Implement new missile entity                                                 | Not started |
| implement new base spells                                                    | Not started |
| Add spell splicing system                                                    | Not started |
| re-impl all spells with new system                                           | Not started |
| overhaul lang with new combining system                                      | Not started |
| Update Enchantment system in MC to account for paired enchants, where needed | Not started |
| Add attribute modifier modification for summons                              | Not started |
| Add on killed event? On completed?                                           | Not started |
| IN PROGRESS                                                                  | 3           |
| Test devout cleric                                                           | In progress |
| Update AC systems to account for paired enchants                             | In progress |
| Overhaul augment datapoint system to simplify and fzzy-config-ify            | In progress |
| DOCUMENTATION TODO                                                           | 4           |
| Polish up wiki                                                               | Doc Todo    |
| Correct updated scepter durabilities in Wiki                                 | Doc Todo    |
| Update all spells in wiki                                                    | Doc Todo    |
| Update enhancing template used in zh_cn version                              | Doc Todo    |
