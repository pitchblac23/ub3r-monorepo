package net.dodian.uber.api

object Animation {
    const val RESET_CHARACTER = -1
    const val PICKUP_DROPOFF = 832
    const val LOOK_AT_MINIMAP_WHEN_FULLSCREEN = 5354
    const val DIG_WITH_SPADE = 831
    const val PUSH_CONTROLS = 3571
    const val PUT_IN_HOPPER = 3572

    const val OPEN_CONTAINER = 536
    const val CLOSE_CONTAINER = 535
    const val OPEN_WARDROBE = 545
    /**
     * Prayer Animations
     */
    const val PRAY_AT_ALTAR = 645
    const val BURY_BONE = 827
    const val OFFER_BONES_TO_ALTER = 3705;
    /**
     * Firemaking Animations
     */
    const val FIREMAKING_TINDERBOX = 733
    /**
     * Cooking Animations
     */
    const val COOKING_ON_FIRE = 897
    const val COOKING_ON_FIRESPIT = 883
    const val COOKING_ON_RANGE = 896
    /**
     * Runecrafting Animations
     */
    const val RUNECRAFTING_CRAFT_RUNES = 791
    /**
     * Herblore Animations
     */
    const val HERBLORE_POTION_MAKING = 363
    const val HERBLORE_CLEAN_HERB = 7531
    /**
     * Fishing Animations
     */
    const val FISHING_NET = 621
    const val FISHING_ROD = 622
    const val FISHING_HARPOON = 618
    const val FISHING_DRAGON_HARPOON = 7401
    const val FISHING_KARAMBWAN = 1193
    const val FISHING_LOBSTER_POT = 619
    /**
     * Woodcutting Animations
     */
    const val WOODCUTTING_BRONZE_AXE = 879
    const val WOODCUTTING_IRON_AXE = 877
    const val WOODCUTTING_STEEL_AXE = 875
    const val WOODCUTTING_BLACK_AXE = 873
    const val WOODCUTTING_MITHRIL_AXE = 871
    const val WOODCUTTING_BLESSED_AXE = 5383
    const val WOODCUTTING_ADAMANT_AXE = 869
    const val WOODCUTTING_RUNE_AXE = 867
    const val WOODCUTTING_DRAGON_AXE = 2846
    const val WOODCUTTING_INFERNAL_AXE = 2117
    const val WOODCUTTING_THIRDAGE_AXE = 7264
    const val WOODCUTTING_CRYSTAL_AXE = 8324
    /**
     * Mining Animations
     */
    const val MINING_BRONZE_PICKAXE = 625
    const val MINING_BRONZE_PICKAXE_FLOOR = 6747
    const val MINING_BRONZE_PICKAXE_WALL = 6753
    const val MINING_IRON_PICKAXE = 626
    const val MINING_IRON_PICKAXE_FLOOR = 6748
    const val MINING_IRON_PICKAXE_WALL = 6754
    const val MINING_STEEL_PICKAXE = 627
    const val MINING_STEEL_PICKAXE_FLOOR = 6749
    const val MINING_STEEL_PICKAXE_WALL = 6755
    const val MINING_BLACK_PICKAXE = 3873
    const val MINING_MITHRIL_PICKAXE = 629
    const val MINING_MITHRIL_PICKAXE_FLOOR = 6751
    const val MINING_MITHRIL_PICKAXE_WALL = 6757
    const val MINING_ADAMANT_PICKAXE = 628
    const val MINING_ADAMANT_PICKAXE_FLOOR = 6750
    const val MINING_ADAMANT_PICKAXE_WALL = 6756
    const val MINING_RUNE_PICKAXE = 624
    const val MINING_RUNE_PICKAXE_FLOOR = 6746
    const val MINING_RUNE_PICKAXE_WALL = 6752
    const val MINING_GUILDED_PICKAXE = 8313 //8358
    const val MINING_GUILDED_PICKAXE_FLOOR = 8314
    const val MINING_GUILDED_PICKAXE_WALL = 8312
    const val MINING_DRAGON_PICKAXE = 7139
    const val MINING_DRAGON_PICKAXE_FLOOR = 7140
    const val MINING_DRAGON_PICKAXE_WALL = 6758
    const val MINING_DRAGON_PICKAXE_U = 642
    const val MINING_DRAGON_PICKAXE_U_WALL = 335
    const val MINING_DRAGON_PICKAXE_O = 8346
    const val MINING_DRAGON_PICKAXE_O_FLOOR = 8349
    const val MINING_DRAGON_PICKAXE_O_WALL = 8344
    const val MINING_DRAGON_PICKAXE_TRAILBLAZER = 8887
    const val MINING_DRAGON_PICKAXE_TRAILBLAZER_FLOOR = 8888
    const val MINING_DRAGON_PICKAXE_TRAILBLAZER_WALL = 8886
    const val MINING_INFERNAL_PICKAXE = 4482
    const val MINING_INFERNAL_PICKAXE_FLOOR = 4483
    const val MINING_INFERNAL_PICKAXE_WALL = 4481
    const val MINING_INFERNAL_PICKAXE_O = 8787 //8789
    const val MINING_INFERNAL_PICKAXE_O_FLOOR = 8788
    const val MINING_INFERNAL_PICKAXE_O_WALL = 8786
    const val MINING_THIRDAGE_PICKAXE = 7283
    const val MINING_THIRDAGE_PICKAXE_FLOOR = 7284
    const val MINING_THIRDAGE_PICKAXE_WALL = 7282
    const val MINING_CRYSTAL_PICKAXE = 8347
    const val MINING_CRYSTAL_PICKAXE_FLOOR = 8350
    const val MINING_CRYSTAL_PICKAXE_WALL = 8345
    /**
     * Smithing Animations
     */
    //NOTE: if having imcando hammer trumps reg hammer animations
    const val SMITHING_HAMMER = 3676
    const val REGULAR_HAMMER = 898
    const val IMCANDO_HAMMER = 8911
    const val SMITHING_SMELT = 896
    /**
     * Crafting Animations
     */
    const val CRAFTING_GLASS_BLOW = 884
    const val CRAFTING_POT_COOK = 1317
    const val CRAFTING_CUT_OPAL = 890
    const val CRAFTING_CUT_JADE = 891
    const val CRAFTING_CUT_REDTOPAZ = 892
    const val CRAFTING_CUT_SAPPHIRE = 888
    const val CRAFTING_CUT_EMERALD = 889
    const val CRAFTING_CUT_RUBY = 887
    const val CRAFTING_CUT_DIAMOND = 886
    const val CRAFTING_CUT_DRAGONSTONE = 885
    const val CRAFTING_CUT_ONYX = 2717
    const val CRAFTING_CUT_ZENYTE = 2717
    /**
     * Fletching Animations
     */
    const val FLETCHING_LOG_CUT = 1248
    const val FLETCHING_SHORTBOW_STRING = 6678
    const val FLETCHING_LONGBOW_STRING = 6684
    const val FLETCHING_OAK_SHORTBOW_STRING = 6679
    const val FLETCHING_OAK_LONGBOW_STRING = 6685
    const val FLETCHING_WILLOW_SHORTBOW_STRING = 6680
    const val FLETCHING_WILLOW_LONGBOW_STRING = 6686
    const val FLETCHING_MAPLE_SHORTBOW_STRING = 6681
    const val FLETCHING_MAPLE_LONGBOW_STRING = 6687
    const val FLETCHING_YEW_SHORTBOW_STRING = 6682
    const val FLETCHING_YEW_LONGBOW_STRING = 6688
    const val FLETCHING_MAGIC_SHORTBOW_STRING = 6683
    const val FLETCHING_MAGIC_LONGBOW_STRING = 6689
    const val FLETCHING_BRONZE_CBOW_U = 4436
    const val FLETCHING_BLUERITE_CBOW_U = 4437
    const val FLETCHING_IRON_CBOW_U = 4438
    const val FLETCHING_STEEL_CBOW_U = 4439
    const val FLETCHING_MITHRIL_CBOW_U = 4440
    const val FLETCHING_ADAMANT_CBOW_U = 4441
    const val FLETCHING_RUNITE_CBOW_U = 4442
    const val FLETCHING_DRAGON_CBOW_U = 7860
    const val FLETCHING_BRONZE_CBOW_STRING = 6680
    const val FLETCHING_BLUERITE_CBOW_STRING = 6686
    const val FLETCHING_IRON_CBOW_STRING = 6681
    const val FLETCHING_STEEL_CBOW_STRING = 6687
    const val FLETCHING_MITHRIL_CBOW_STRING = 6682
    const val FLETCHING_ADAMANT_CBOW_STRING = 6688
    const val FLETCHING_RUNITE_CBOW_STRING = 6683
    const val FLETCHING_DRAGON_CBOW_STRING = 6689
    /**
     * Farming Animations
     */
    const val FARMING_WATERING_CAN = 2293
    const val FARMING_RAKING = 2273
    const val FARMING_SEED_DIBBING = 2291
    const val FARMING_PICKING_VEGETABLE = 2282
    const val FARMING_PICKING_HERB = 2279
    const val FARMING_CURING = 2288
    const val FARMING_FILLING_POT = 2287
    const val FARMING_PLANTING_POT = 2272
    const val FARMING_PRUNING = 2275
    /**
     * Hunter Animations
     */
    const val HUNTER_TRAP = 827
    const val HUNTER_BUTTERFLY_NET = 6606
    /**
     * Thieving Animations
     */
    const val THIEVING_PICKPOCKET = 881
    const val THIEVING_STALL = 832
    /**
     * Agility Animations
     */
    const val AGILITY_CLIMB_UP = 828
    const val AGILITY_CLIMB_DOWN = 827
    const val AGILITY_LOG_WALK = 7134
    const val AGILITY_ROPE_SWING = 751
    const val AGILITY_PULL_UP = 2585
    const val AGILITY_JUMP_DOWN = 2586
    const val AGILITY_LAND_DOWN = 2688
    const val AGILITY_PREPARE_ZIP = 1601
    const val AGILITY_ZIP_LINE = 1602
    const val AGILITY_CLIMB_WALL = 840
    const val AGILITY_CROSS_LEDGE_RIGHT = 756
    const val AGILITY_CROSS_LEDGE_LEFT = 754
    const val AGILITY_JUMP = 3067
    const val AGILITY_STEPPING_STONE = 741
    const val AGILITY_PIPE_START =746
    const val AGILITY_PIPE_CRAWL = 749
    const val AGILITY_PIPE_EXIT = 748
    /**
     * Magic Animations
     */
    const val CAST_MAGIC_SPELL = 1979
    /**
     * Ladder Animations
     */
    const val CLIMB_LADDER = 828
    const val CLIMB_DOWN = 827
    /**
     * Blast Furnace Animations
     */
    //NOTE: pipe repair is smithing animations.
    const val PUMP_FURNACE = 2432
    const val BELT_PEDAL = 2433
    const val COKE_COLLECT = 2441
    const val STOVE_FILL = 2442
    const val STOVE_RESET = 2443
    const val WATER_BARS = 2450
    /**
     * Giants Foundry
     */
    const val DUNK_SWORD = 827
    const val COOL_SWORD = 832
    const val FILL_CRUCIBLE = 4909
    const val POUR_CRUCIBLE = 810
    const val POLISH_START = 9452
    const val WORK_HAMMER = 9455
    const val HAMMER_START = 9453
    const val WORK_SWORD = 9454
    const val SWORD_FINISH = 9457
}