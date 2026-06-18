package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Detailed Local Place Data Model
data class Place(
    val id: String,
    val nameTr: String,
    val nameEn: String,
    val type: PlaceType,
    val descriptionTr: String,
    val descriptionEn: String,
    val audioDurationTr: String = "2:15",
    val audioDurationEn: String = "2:30",
    val audioScriptTr: String,
    val audioScriptEn: String,
    val latitude: Double,
    val longitude: Double,
    val qrCodeId: String,
    val imageUrlPlaceholder: String = "" // Will handle using custom Canvas drawing or stylized dynamic material art!
)

enum class PlaceType {
    HISTORICAL, NATURE, MUSEUM
}

// Nasreddin Hodja Story Data Model
data class HodjaStory(
    val id: String,
    val titleTr: String,
    val titleEn: String,
    val storyTr: String,
    val storyEn: String,
    val wisdomTr: String,
    val wisdomEn: String,
    val audioDuration: String = "1:45"
)

// Local Flavor (Food) Data Model
data class Food(
    val id: String,
    val nameTr: String,
    val nameEn: String,
    val descriptionTr: String,
    val descriptionEn: String,
    val ingredientsTr: List<String>,
    val ingredientsEn: List<String>,
    val recipeTr: String,
    val recipeEn: String,
    val bestPlacesTr: String,
    val bestPlacesEn: String
)

// Main Travel Repository combining offline persistence and authentic travel guide datasets
class TravelRepository(private val travelDao: TravelDao) {

    // Database access
    val favoriteRoutes: Flow<List<FavoriteRouteEntity>> = travelDao.getAllFavoriteRoutes()
    val favoritePlaces: Flow<List<FavoritePlaceEntity>> = travelDao.getAllFavoritePlaces()

    suspend fun saveRoute(name: String, pointsJson: String, dist: Double, duration: Int) {
        travelDao.insertFavoriteRoute(
            FavoriteRouteEntity(
                routeName = name,
                routePointsJson = pointsJson,
                totalDistanceKm = dist,
                totalDurationMin = duration
            )
        )
    }

    suspend fun removeRoute(routeId: Int) {
        travelDao.deleteFavoriteRoute(routeId)
    }

    suspend fun togglePlaceFavorite(place: Place, note: String = ""): Boolean {
        val current = travelDao.getFavoritePlaceById(place.id)
        return if (current != null) {
            travelDao.deleteFavoritePlace(place.id)
            false
        } else {
            travelDao.insertFavoritePlace(
                FavoritePlaceEntity(
                    placeId = place.id,
                    placeNameTr = place.nameTr,
                    placeNameEn = place.nameEn,
                    type = place.type.name,
                    note = note
                )
            )
            true
        }
    }

    suspend fun isPlaceFavorite(placeId: String): Boolean {
        return travelDao.getFavoritePlaceById(placeId) != null
    }

    // --- STATIC AUTHENTIC AKŞEHİR TRAVEL DATASET ---

    val places = listOf(
        Place(
            id = "tomb_nasreddin_hodja",
            nameTr = "Nasreddin Hoca Türbesi",
            nameEn = "Tomb of Nasreddin Hodja",
            type = PlaceType.HISTORICAL,
            descriptionTr = "Gülmece ve felsefe dünyasının en büyük bilgelerinden Nasreddin Hoca'nın ebedi istirahatgahı olan bu türbe, meşhur 'ters asma kilitli açık kapısı' ile dünyaca ünlüdür. Her yıl binlerce seyyah ve güler yüzlü insanı ağırlar.",
            descriptionEn = "The final resting place of Nasreddin Hodja, the great philosopher of Turkish humor. Known globally for its iconic 'open tomb with a padlocked front gate' which embodies his satire and wisdom.",
            audioScriptTr = "Şu anda Nasreddin Hoca'nın dünyaca ünlü türbesindesiniz. Dikkat ederseniz türbenin ana kapısı kilitli iken diğer tüm yanları tamamen açıktır. Bu, felsefesinin şakacı ve açık sözlü doğasını simgeler. Hoca, dünyaya her zaman gülümsemeyi ve olaylara tersten bakıp ders çıkarmayı öğütlemiştir.",
            audioScriptEn = "You are currently at the world-famous Tomb of Nasreddin Hodja. Notice how the main gate is locked with a giant padlock while all other sides are completely open. This symbolizes his witty philosophy: life has restrictions but also endless openings. Smile and think!",
            latitude = 38.3582,
            longitude = 31.4244,
            qrCodeId = "QR_HODJA_TOMB"
        ),
        Place(
            id = "west_front_museum",
            nameTr = "Batı Cephesi Karargahı Müzesi",
            nameEn = "West Front Headquarters Museum",
            type = PlaceType.MUSEUM,
            descriptionTr = "Kurtuluş Savaşı sırasında Büyük Taarruz planlarının hazırlandığı ve koordine edildiği tarihi binadır. Mustafa Kemal Atatürk ve İsmet İnönü gibi liderlerin tarihi kararlar aldığı karargah odaları sergilenmektedir.",
            descriptionEn = "The historical headquarters building where the plans for the Great Offensive (Büyük Taarruz) were drawn up during the Turkish War of Independence. Rooms of Mustafa Kemal Atatürk and commanders are preserved.",
            audioScriptTr = "Burası Kurtuluş Savaşı'nın kaderinin çizildiği Batı Cephesi Karargahı Müzesidir. Atatürk ve komutanları, düşman birliklerini şaşırtacak taarruz stratejilerini bu binada geceler boyunca tartışarak tasarlamışlardır. Akşehir, bu açıdan tarihi zafere ev sahipliği yapmıştır.",
            audioScriptEn = "This is the West Front Headquarters Museum, where the fate of the Independence War was decided. Atatürk and his commanders spent sleepless nights designing the strategic offense. Akşehir proved to be the shield and heart of the nation.",
            latitude = 38.3565,
            longitude = 31.4162,
            qrCodeId = "QR_WEST_FRONT"
        ),
        Place(
            id = "stone_madrasa",
            nameTr = "Taş Medrese Archeology Müzesi",
            nameEn = "Stone Madrasa Archeological Museum",
            type = PlaceType.MUSEUM,
            descriptionTr = "Selçuklu dönemine ait, muazzam taş işlemeleri ve ders salonlarıyla ünlü 1250 yapımı medresedir. Bugün Roma, Bizans, Selçuklu Dönemlerine ait mezar stelleri, kitabeler ve taş heykeller sergilenmektedir.",
            descriptionEn = "A magnificent Seljuk enterprise built in 1250, famous for its intricate stone carvings. It now serves as an archaeological museum, housing Roman, Byzantine, and Seljuk stone artifacts.",
            audioScriptTr = "Selçuklu mimarisinin görkemli taş işçiliğine sahip Taş Medrese'desiniz. Açık avlulu mimarisi ve mescit kapısındaki çini süslemeleri, o dönemin eğitim merkezlerine verdikleri sanatsal ve ilmi değeri gözler önüne seriyor.",
            audioScriptEn = "You stand in Stone Madrasa, built in 1250 during the Seljuk Empire. The stunning open courtyard and mosaic tiles show the advanced state of arts, science, and scholastic life in medieval Anatolia.",
            latitude = 38.3551,
            longitude = 31.4116,
            qrCodeId = "QR_STONE_MADRASA"
        ),
        Place(
            id = "rustu_bey_mansion",
            nameTr = "Rüştü Bey Konağı Etnografya Müzesi",
            nameEn = "Rüştü Bey Mansion Ethnography Museum",
            type = PlaceType.MUSEUM,
            descriptionTr = "Geç Osmanlı mimarisinin en zarif örneklerinden biri olan ve restore edilen tarihi konak. Akşehir'in geleneksel ev yaşantısı, gelin odaları, yerel kıyafetler ve el sanatları balmumu heykellerle canlandırılmıştır.",
            descriptionEn = "A beautifully restored Late Ottoman mansion demonstrating traditional life in Akşehir. Exquisite wooden ceilings, wedding chambers, historic clothing and crafts are revived using wax figures.",
            audioScriptTr = "Rüştü Bey Konağı, Akşehir'in köklü aile yaşamını ve kültürel birikimini barındırır. Ahşap tavan işlemeleri ve sofası, Osmanlı döneminin konfor anlayışını ve geniş misafirperverliğini yansıtmaktadır.",
            audioScriptEn = "Rüştü Bey Mansion displays late Ottoman interior comfort. Its high ceilings, central hall, and historical traditional rugs capture the cozy, generous hospitality of Akşehir families.",
            latitude = 38.3533,
            longitude = 31.4135,
            qrCodeId = "QR_RUSTU_MANSION"
        ),
        Place(
            id = "hayrani_tomb",
            nameTr = "Seyyid Mahmud Hayrani Türbesi",
            nameEn = "Seyyid Mahmud Hayrani Tomb",
            type = PlaceType.HISTORICAL,
            descriptionTr = "Mevlana Celaleddin Rumi'nin çağdaşı, meşhur mutasavvıf Seyyid Mahmud Hayrani'ye ait görkemli Selçuklu türbesi. Özellikle ahşap oymacılık harikası olan sandukası dünyaca meşhurdur.",
            descriptionEn = "The grand Seljuk tomb of Seyyid Mahmud Hayrani, a contemporary of Jalaladdin Rumi. Its spectacular carved wood sarcophagus is celebrated globally as a masterpiece of artisanal woodcraft.",
            audioScriptTr = "Seyyid Mahmud Hayrani, Akşehir'in ruhani koruyucularından ve sufizmin büyük temsilcilerindendir. Türbesinin kubbesi Selçuklu sırlı tuğla süslemesiyle asırlara meydan okuyan bir sadelik sunmaktadır.",
            audioScriptEn = "Seyyid Mahmud Hayrani is the spiritual beacon of Akşehir. The tomb's beautiful brickwork dome has stood the test of centuries, attracting seekers of peace and architecture enthusiasts alike.",
            latitude = 38.3508,
            longitude = 31.4122,
            qrCodeId = "QR_HAYRANI_TOMB"
        ),
        Place(
            id = "aksehir_lake",
            nameTr = "Akşehir Gölü ve Maya Çalma Parkı",
            nameEn = "Akşehir Lake and Maya Park",
            type = PlaceType.NATURE,
            descriptionTr = "Nasreddin Hoca'nın 'Ya tutarsa?' diyerek yoğurt mayaladığı efsanevi Sulak Alan gölüdür. Bugün göçmen kuşların geçiş güzergahı ve harika bir doğal dinlenme mekanı imkanı sunar.",
            descriptionEn = "The legendary lake where Nasreddin Hodja symbolically fermented 'just in case it works!'. Today, it offers peaceful panoramic views and acts as a key wetland for migratory birds.",
            audioScriptTr = "Şu anda Akşehir Gölü kıyısındasınız. İşte burası Hoca'nın elinde yoğurt mayasıyla gelip 'Göle maya çalınır mı?' diyenlere 'Ya tutarsa!' cevabını verdiği, imkansız olana dahi umutla ve tebessümle bakmayı öğreten yerdir.",
            audioScriptEn = "Welcome to Akşehir Lake. This is where Hodja carried his yeast bowl to ferment the whole lake. Asked if it could ever ferment, his classic reply 'What if it does?' teaches a powerful lesson of hope and humor.",
            latitude = 38.4831,
            longitude = 31.4554,
            qrCodeId = "QR_AKSEHIR_LAKE"
        )
    )

    val stories = listOf(
        HodjaStory(
            id = "story_fermenting_lake",
            titleTr = "Ya Tutarsa? (Göle Maya Çalmak)",
            titleEn = "What if it Works? (Yeast in the Lake)",
            storyTr = "Nasreddin Hoca bir gün elinde koca bir kase yoğurt mayasıyla gölün kıyısına inmiş. Kaşık kaşık mayayı göle dökmeye başlamış. Bunu gören köylüler şaşkınlıkla fırlayıp, 'Hoca Efendi, ne yapıyorsun? Koskoca göl maya tutar mı hiç?' demişler. Hoca tebessüm etmiş ve tarihe geçen o cevabı vermiş: 'Ben de bilirim tutmayacağını, ama ya tutarsa?'",
            storyEn = "One day, Nasreddin Hodja went down to Akşehir lake with a large bowl of yogurt starter. He began spoonful by spoonful pouring it into the lake. Astounded, the villagers rushed to him and asked, 'Teacher, what on earth are you doing? Can a huge lake ever turn into yogurt?' Hodja smiled and replied, 'I know it won't, but what if it does?'",
            wisdomTr = "Hoca bu fıkrasıyla bizlere her koşulda umutlu olmayı, hayal kurmaktan korkmamayı ve imkansız görünen hedeflere sevgiyle inanmayı öğütler.",
            wisdomEn = "Hodja teaches us never to lose hope, to dare to dream, and to believe in impossible targets with deep faith and a joyous heart."
        ),
        HodjaStory(
            id = "story_reverse_donkey",
            titleTr = "Eşeğe Neden Ters Bindi?",
            titleEn = "Riding the Donkey Backwards",
            storyTr = "Bir gün Hoca eşeğine binerken yüzü arkaya, arkası öne gelecek şekilde binmiş. Cemaat şaşırmış: 'Hoca Efendi, eşeğe niye ters binersin? Arkası önü karıştırdın herhalde!' Hoca ise sakalını sıvazlamış ve 'Hayır, bilerek böyledir cemaat,' demiş. 'Eğer düz binip önden gitsem, siz arkada kalacaksınız ve arkam size dönecek; hürmetsizlik olacak. Eğer siz öne geçseniz, bu kez ben arkada kalıp sizi göremeyeceğim. En iyisi bu: ben ters biniyorum ki hem birlikte gidelim hem de göz göze sohbet edebilelim!'",
            storyEn = "Once, Hodja mounted his donkey looking backward. The bewildered crowd asked: 'Teacher Hodja, why are you riding backward? Did you make a mistake?' Stroking his beard, Hodja said, 'Not at all, my friends. If I rode forward, my back would be towards you, which is disrespectful. If you went in front, I couldn't see you. Riding backwards is the absolute best way: compiled together, we go forward, yet stay face-to-face in friendly chat!'",
            wisdomTr = "Karşılıklı saygı, iletişim ve empati kurmanın, dış görünüşten her zaman daha değerli olduğunu gösteren mizahi bir inceliktir.",
            wisdomEn = "A gentle, hilarious lesson demonstrating that mutual respect, communication, and empathy are always more precious than external social appearances."
        ),
        HodjaStory(
            id = "story_cauldron",
            titleTr = "Kazan Doğurdu ve Kazan Öldü",
            titleEn = "The Cauldron Gave Birth & Died",
            storyTr = "Nasreddin Hoca bir gün komşusundan büyük bir bakır kazan ödünç almış. İşi bitince kazanın içine küçük bir tencere koyup iade etmiş. Komşusu 'Bu ne?' deyince, 'Komşu, müjde! Senin kazan bende kaldı, doğurdu!' demiş. Komşu sevinçle iki kabı da almış. Birkaç hafta sonra Hoca kazanı yine ödünç almış ama bu sefer uzun süre geri getirmemiş. Komşu kazanı istemeye gelince Hoca ağlamaya başlamış: 'Komşu başın sağ olsun, senin kazan öldü!' Komşu öfkeyle 'Hadi canım, kazan hiç ölür mü cansız şey?' deyince, Hoca 'Kazanın doğurduğuna inanıyorsun da öldüğüne neden inanmıyorsun?' demiş.",
            storyEn = "Hodja borrowed a large copper cauldron from his neighbor. When returning it, he placed a small pot inside. Excitingly, he told the owner: 'Good news! Your cauldron gave birth at my place!' Delighted, the neighbor accepted both. Weeks later, Hodja borrowed it again but didn't return it. Visited by the neighbor, Hodja began weeping: 'So sorry, your cauldron has died!' Furious, the neighbor yelled, 'Nonsense! Can a cauldron ever die?' Hodja calmly replied, 'You believed it gave birth, why deny it when it dies?'",
            wisdomTr = "İnsanların çıkarlarına uyan olayları kolayca kabullenip, çıkarlarına aykırı gerçekleri ise inkar etmelerindeki bencilliği vurucu bir zeka ile yüzleştirir.",
            wisdomEn = "A brilliant exposition of human greed: how easily people accept events that satisfy their own self-interest, yet fiercely deny the natural laws when things go against them."
        ),
        HodjaStory(
            id = "story_piper",
            titleTr = "Parayı Veren Düdüğü Çalar",
            titleEn = "He Who Pays the Piper Calls the Tune",
            storyTr = "Hoca pazara doğru giderken çocuklar etrafını sarmış. Her biri: 'Hoca, bana pazardan oyuncak al,' 'Bana şeker al,' demişler. Ama içlerinden sadece biri Hoca'ya bir gümüş sikke uzatarak 'Pazardan bana bir düdük alıver,' demiş. Hoca akşam üzeri pazardan dönünce çocuklar kapıda toplanmış. Hoca elini cebine atmış ve parayı uzatan çocuğa düdüğü vermiş. Diğerleri şikayet edince, 'Eee çocuklar,' demiş, 'Parayı veren düdüğü çalar!'",
            storyEn = "As Hodja headed to the marketplace, children crowded around him, requesting toys, sweets, and whistles. But only one boy handed him a silver coin, asking for a whistle. At dusk, as Hodja returned, the children flocked around him. Hodja reached into his pocket and handed the whistle only to the boy who paid. When others complained, Hodja famously declared, 'Well, children, he who pays the copper silver gets the whistle!'",
            wisdomTr = "Emek verilmeden elde edilmeye çalışılan beklentilerin anlamsızlığını ve adaletin ancak sorumluluk bilinciyle sağlanabileceğini mizahi bir dille söyler.",
            wisdomEn = "A classic instruction showing that expectations without real investments or efforts are empty, and justice can only be maintained with absolute responsibility."
        )
    )

    val foods = listOf(
        Food(
            id = "food_tandir_kebap",
            nameTr = "Akşehir Tandır Kebabı",
            nameEn = "Akşehir Lamb Tandir Kebab",
            descriptionTr = "Akşehir'in en seçkin tescilli lezzetidir. Özel taş fırınlarda, meşe odunu ateşi eşliğinde 4-5 saat boyunca kendi yağıyla nar gibi kızaran taptaze kuzu eti, sıcacık pide (tırnak pidesi) üzerinde servis edilir. Olağanüstü bir yumuşaklığa sahiptir.",
            descriptionEn = "The most celebrated culinary hallmark of Akşehir. Fresh lamb is slow-cooked for 4 to 5 hours in custom stone ovens using oak firewood. Served tender and sizzling over freshly baked flatbread.",
            ingredientsTr = listOf("Taze kuzu eti (ön kol ve kaburga)", "Akşehir tırnak pidesi", "Özel kaya tuzu", "Sarımsaklı veya közlenmiş biber/domates garnitürü"),
            ingredientsEn = listOf("Fresh young lamb meat", "Traditional Akşehir flatbread (pide)", "Natural rock salt", "Roasted green peppers and tomatoes"),
            recipeTr = "Kuzu etleri kaya tuzu ile ovulduktan sonra bakır tepsilerde meşe odunlu taş fırında saatlerce demlenerek pişirilir. Lokum kıvamına gelen etler, pide üzerinde kendi öz fırın yağı dökülerek sıcak sıcak ikram edilir.",
            recipeEn = "Lamb parts are massaged with rock salt and slow-roasted in copper trays over oak logs. The meat melts off the bone. Juices are drizzled over flatbread, topped with piping hot meat.",
            bestPlacesTr = "Tarihi Arasta Çarşısı Tandır Kebapçıları",
            bestPlacesEn = "Historic Arasta Bazaar Master Kebap Chefs"
        ),
        Food(
            id = "food_aksehir_kofte",
            nameTr = "Akşehir Köftesi",
            nameEn = "Akşehir Heritage Meatballs",
            descriptionTr = "Dana ve kuzu kıymasının özel harç, baharatlar ve soğan suyuyla dinlendirilerek kömür ızgarasında pişirilmesiyle yapılan, ısırıldığında ağızda sularını salan muazzam derecede lezzetli bir köftedir.",
            descriptionEn = "A mouthwatering blend of beef and lamb minces, spiced with domestic recipes and onion juice, grilled on real charcoal to preserve its delicious juicy texture.",
            ingredientsTr = listOf("Yarı dana yarı kuzu kıyması", "Rendelenmiş soğan suyu", "Kimyon, karabiber, tatlı toz biber", "Ekmek içi hamuru"),
            ingredientsEn = listOf("Half beef, half lamb finely ground", "Onion juice discard pulp", "Cumin, black pepper, sweet ground pepper", "Soaked artisan breadcrumbs"),
            recipeTr = "Kıyma, baharatlar ve soğan suyu 30 dakika boyunca mükemmelce yoğrulur ve buzdolabında 1 gün dinlendirilir. Parmak şeklinde şekillendirilip hararetli kömür ateşinde kurutulmadan içi sulu kalacak şekilde pişirilir.",
            recipeEn = "The meat is kneaded with spices and onion extract for 30 minutes, resting in the fridge for 24 hours. Shaped into cylinders, it's grilled on high charcoal heat to preserve a super juicy core.",
            bestPlacesTr = "Akşehir İlçe Parkı Yanı Köfte Evi, Merkez Lokantaları",
            bestPlacesEn = "Merkez Diner and Park-side Grillhouses"
        ),
        Food(
            id = "food_peynir_helvasi",
            nameTr = "Akşehir Peynir Baklavası (Helvası)",
            nameEn = "Akşehir Cheese Baklava (Halva)",
            descriptionTr = "Akşehir'de taze tuzsuz peynirden yapılan, irmik, un ve tereyağı ile fırınlanan sıcak ve kadife dokulu harika bir yerel tatlıdır. Birçok yörede helva denser de Akşehir'de 'peynir baklavası' adıyla anılır.",
            descriptionEn = "A legendary dessert crafted using fresh, unsalted local cheese, baked to golden brown with flour, butter, and semolina. Despite the name, it has a hot, velvety cheese-cake texture.",
            ingredientsTr = listOf("Taze tuzsuz Akşehir koyun peyniri", "Organik tereyağı", "Un ve irmik", "Toz şeker ve su şerbeti"),
            ingredientsEn = listOf("Fresh unsalted local sheep cheese", "Pure organic butter", "Flour and semolina", "Sugar syrup"),
            recipeTr = "Peynir tereyağı ile birlikte ocakta eritilir. Un ve irmik ilave edilerek sürekli karıştırılarak kavrulur. Karışım hafif pembeleşince şerbeti dökülür ve fırınlanarak sıcak, hafif akışkan kıvamda servis edilir.",
            recipeEn = "Cheese is melted with butter in a hot pan. Flour and semolina are stirred continuously until toasted, followed by hot syrup. Baked under high heat for a golden crust while keeping the inside cheesy.",
            bestPlacesTr = "Hacı Şerif Meydan Pastanesi, Arasta Helvacıları",
            bestPlacesEn = "Grand Pastry of Arasta Square, local halva masters"
        )
    )
}
