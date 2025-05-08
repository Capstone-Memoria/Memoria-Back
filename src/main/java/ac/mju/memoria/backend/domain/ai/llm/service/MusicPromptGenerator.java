package ac.mju.memoria.backend.domain.ai.llm.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface MusicPromptGenerator {

  @SystemMessage("""
          # AI Agent Instructions: Diary-Based Music Genre Prompt Generation
          
          **Objective:**
          To analyze the content of an input diary entry and generate the most suitable music genre prompt that reflects its mood and content. The generated prompt will be structured for use with music generation AIs.
          
          **Procedure:**
          
          1.  **Diary Content Analysis:**
              * Read the diary text to thoroughly understand its core emotions, atmosphere, key events, and mentioned subjects.
              * Examples: Joy, sadness, excitement, loneliness, calmness, specific activities (exercise, study, travel, party), relationships, weather, time of day, etc. Consider all elements of the diary.
          
          2.  **Tag Selection from `Preset Dictionary`:**
              * Refer to the provided `Preset Dictionary` and **mandatorily select tags for all five (5) of the following components**: `genre`, `instrument`, `mood`, `gender`, and `timbre`. All five elements must be included for stable and consistent prompt generation.
              * **`genre`:** From the `genre` list in the `Preset Dictionary`, select the music genre that best matches the overall mood, narrative, and emotional arc of the diary. (e.g., for a happy and cheerful diary -> "Pop", "Dance"; for an emotional and calm diary -> "Ambient", "Classical"; for intense and dynamic content -> "Rock", "Electronic")
              * **`instrument`:** From the `instrument` list in the `Preset Dictionary`, select the main instrument(s) that can effectively express the chosen genre and the specific atmosphere of the diary. (e.g., "Pop" genre -> "Synthesizer", "Drums"; "Classical" genre -> "Piano", "Violin"; "Rock" genre -> "Electric guitar", "Bass")
              * **`mood`:** From the `mood` list in the `Preset Dictionary`, select the tag(s) that directly represent the most prominent emotion(s) or atmosphere in the diary. If multiple emotions are present, prioritize the core emotion/mood or combine multiple tags. (e.g., for happy and hopeful content -> "happy", "uplifting", "Hopeful"; for sad and nostalgic content -> "sad", "melancholic", "Nostalgic")
          
          3.  **Prompt Combination:**
              * Combine the selected tags for the five components into a single string, separated by spaces.
              * The order of the tags can be flexible. Actively refer to the structure and order of the provided example prompt: `"inspiring female uplifting pop airy vocal electronic bright vocal vocal"`. This example is structured as [mood], [gender], [mood], [genre], [timbre], [instrument], [timbre], [timbre/vocal emphasis], demonstrating that specific elements (like `mood` and `timbre`) can utilize multiple tags.
          
          4.  **Special Tags (If Applicable):**
              * If the diary is written in Chinese (Mandarin or Cantonese), add "Mandarin" or "Cantonese" tag at the beginning or end of the prompt after content analysis. Even in this case, the five basic components must still be included in the prompt.
          
          **Rules for Using `Preset Dictionary`:**
          * When generating prompts, you **must exclusively use words from the provided `Preset Dictionary`** to form the tags for each component.
          * Carefully select the most appropriate word(s) from each category (`genre`, `instrument`, `mood`, `gender`, `timbre`) that best fit the diary's content and mood.
          
          **Output Example (Based on a hypothetical diary entry):**
          
          * **Diary Content:** "Today was such an exciting day! I went to the amusement park with my friends and finally rode the rollercoaster I've always wanted to. It moved so fast, my heart felt like it was going to explode, but the thrill was so good I kept screaming. My legs were wobbly when I got off, but I felt on top of the world. We ate delicious food in the evening and saw the stars in the night sky. Everything was perfect."
          * **Generated Prompt (Expected):** happy upbeat energetic Pop synthesizer
              * **Analysis:**
                  * `mood`: happy, upbeat, energetic (reflecting the diary's exciting and vibrant atmosphere)
                  * `genre`: Pop (a popular and bright genre)
                  * `instrument`: synthesizer (fits the Pop genre and emphasizes a modern, exciting feel)
         
         **examples**
         - Sad varied Country Folk full Piano Serious
         - romantic keyboard soul emotional blues classic rock guitar bass drums
         - female blues  piano sad romantic guitar jazz
         
          **References:**
          * `Preset Dictionary`
          ```yaml
          genre: ["Pop", "rock", "pop", "electronic", "Classical", "R&B", "Electronic", "Rock", "Folk", "rap", "classical", "soundtrack", "country", "indie-rock", "punk", "hiphop", "folk", "jazz", "Country", "hip-hop", "Hip-hop", "experimental", "Hip Hop", "Funk", "blues", "ambient", "Rap", "Jazz", "Ambient", "New Age", "Blues", "experimental pop", "classic rock", "indie rock", "alternative rock", "Reggae", "Electro pop", "K-pop", "Dance", "Soundtrack", "Hip hop", "80s", "Dancehall", "Disco", "House", "Death Metal", "Thrash Metal", "international", "progressive rock", "hard rock", "instrumental", "Lounge", "house", "Latin", "hardcore", "Metalcore", "Soul", "grunge", "Easy listening", "easylistening", "Indian", "ethno", "Hard rock", "hip hop", "Indie Pop", "Electro", "industrial", "grindcore", "post-rock", "Soul-R&B", "Reggaeton", "World", "latin pop", "Classic Rock", "Latin pop", "Deathcore", "soul", "improvisation", "Chinese", "techno", "Salsa", "indie pop", "Hardcore", "拉丁", "Black metal", " Americana", "dance", "rock nacional", "tejano", "indie", "ambient electronic", "world", "Death metal", "Trap", "avant-garde", "Chillout", "Americana", "new wave", "rnb", "pop rock", "post-hardcore", "singer-songwriter", "pop punk", "Power metal", "indie folk", "opera", "Metal", "African", "instrumental rock", "Gospel", "downtempo", "New Wave", "Electro-pop", "rockabilly", "MPB", "goth rock", "soul-R&B", "Black Metal", "Dubstep", "Eurovision", "Bossa Nova", "bossanova", "民谣", "big band", "Synthpop", "死亡金属", "中国传统音乐", "glam rock", "国际音乐", "latin", "operatic", "Melodic Death Metal", "lounge", " Regional Mexican", "instrumental pop", "emo", "旋律死亡金属", "Pop Rock", "popfolk", " Latin", "poprock", "eurovision", "Ska", "Techno", "disco", "基督教音乐", "Indie rock", "Goregrind", "8-bit", "Pop rock", "screamo", "Dance pop", "Guitar", "chillout", "beats", "Big band", "mpb", "Bluegrass", "流行", "Thrash metal", "easy listening", "Samba", "Heavy metal", "Symphonic metal", "Chanson", "Oriental", "synthpop", "Girl group", "Epic", "Celtic", "Screamo", "Espanol", "Middle Eastern", "electro", " Soul-R&B", " Classic Rock", "Heavy Metal", "dubstep", "民乐", "country rock", "funk", "ska", "Indie Rock", "Choral", "J-rock", "shoegaze", "Rockabilly", "grime", "Italian pop", "摇滚", " latin", "Bolero", " orchestral", "experimental hip-hop", "eurodance", "noise rock", "electro pop", "noise", "Crossover Country", "Glitch"]
          instrument: ["Piano", "drums", "guitar", "electric guitar", "Guitar", "synthesizer", "Synthesizer", "Keyboard", "piano", "Drums", "Violin", "bass", "acoustic guitar", "Bass", "violin", "voice", "vocal", "acousticguitar", "Electric guitar", "Acoustic guitar", "electricguitar", "Voice", "keyboard", "saxophone", "beat", "Drum machine", "Cello", "harmonica", "fiddle", "Percussion", "beatboxing", "Vocal", "鼓", "Saxophone", "keys", "harp", "Keyboards", "keyboards", " harmonica", "singing", "吉他", "贝斯", "钢琴", "beats", "flute", "bass guitar", "drum", "brass", "Flute", "Fiddle", "charango", "Sitar", "strings", "trumpet", "Brass", "Vocals", "Trumpet", "string", "Singing", " banjo", "drum machine", "cello", "Acoustic Guitar", "glockenspiel", "computer", "电吉他", "合成器", "键盘", "mallets", "原声吉他", "Drum", "Bass guitar", "Dholak", "congas", "Electric Guitar", "二胡", "鼓机", "synth", "Strings", "小提琴", "Trombone", "percussion", "弦乐", "electricpiano", "风琴", "oboe", "horns", "Erhu", " synthesizer", "acoustic drums", " pedal steel guitar", " Voice", "Tambourine", "singer-songwriter", "Oud", "Qanun", "electronic", " pedal steel", "rapping", "Funky bass", "guitars", "木吉他", "Alto saxophone", "Ukulele", "扬琴", "oud", "sitar", "打击乐器", "Synth", "organ", "Kanun", "人声", "古筝", " accordion", "bandura", "banjo", "长笛", "pandeira", "turntables", "Alto Saxophone", " slideguitar", " electricguitar", "rap", "harpsichord", "萨克斯管", "maracas", "口琴", "Guitars", "Dobro guitar", "vocals", "choir", "Ableton", "Horns", "AcousticGuitar", "笛子", "synth drums", "Glockenspiel", "Harp", "zither", "Dobro", "Musical instrument", "electric piano", "竖琴", "Horn", "手风琴", "None", "Choir", "铜管乐器", "String", "vocal samples", "trombone", "班卓琴", "hu lu si", "Pandeira", "采样器", " Banjo", "Synth bass", "synth bass", "mallet", " tabla", "dulcimer", "声乐", "Cavaquinho", "大提琴", "toms", "ney", " trumpet", " voice", "低音", "Zither", "shakuhachi", "主唱", " electric guitar", "tambourine", "Turntables", "lyrics", " concertina", " piano", " steel guitar", "Bongos", "Koto", "808 bass", "Marimba", " drums", "Dance", "萨克斯风", "木琴", " bass", "ukulele", "Steel pan", "女声", "键盘乐器", "whistle", "soprano saxophone", "Nylon string guitar", "synth_lead", "电脑", "Shakuhachi", "oboes", "Rap"]
          mood: ["Uplifting", "emotional", "uplifting", "happy", "Inspiring", "romantic", "sad", "Love", "melancholic", "dark", "Upbeat", "Energetic", "Romantic", "Melancholic", "Nostalgic", "Calm", "Hopeful", "melodic", "relaxing", "Romance", "Emotional", "Dreamy", "energetic", "rebellious", "Dance", "inspiring", " introspective", "Confident", "aggressive", "Positive", "calm", "cool", "Happy", "hopeful", "beautiful", "advertising", "angry", "Sad", "relaxed", "Celebratory", "Angry", "Bold", "Introspective", "Optimistic", "sentimental", "optimistic", "Tough", "motivational", "Heartfelt", "Funky", "communication", "Danceable", "vivacious", "love", "commercial", "Vivacious", "heavy", "ballad", "thoughtful", "fast-paced", "Futuristic", "Joyful", "emotion", "Soulful", "attitude", "positive", "epic", "Festive", "Melodic", "Dancy", "Aggressive", "soft", "Calming", "exciting", "dreamy", "Epic", "nostalgic", "powerful", "adventure", "passionate", "Determined", "沟通", "Sensual", "Playful", "street", "heartfelt", "Rebellious", "intense", "Sentimental", "inspirational", "travel", "Adventurous", "atmospheric", "summer", "easygoing", "Cheerful", "Cool", "Dark", "rock", "Inspiration", "Chill", "Intense", "confident", "empowering", "Violent", "Intimate", "longing", " meditative", "Attitude", "romance", "experimental", "at sea", "放松", "chill", "Exciting", "Soothing", "Empowering", "暴力", "Brawny", "cheerful", "Motivational", "Vibraphone", "tough", "determined", "hardcore", "Reflective", "funny", "Peaceful", "loud", "Pensive", "向上", "playful", "Furious", "时尚", "希望", "rough", "Intimacy", "dance", "Vibrant", "Relaxed", "soundscape", "Brutal", "thought-provoking", "success", "sleepy", "Elegant", "children", "intimate", "残酷", "怀旧", "improvisational", "浪漫", "Ambient", "Affectionate", "Gory", "Dramatic", "enthusiastic", "感性", "ambient", "Gentle", "愤怒", "快乐", "黑暗", "brawny", "Seductive", "Dancing", "introspective", "instrumental", "Satisfied", "hard", "史诗", " documentary", " dreamy", "Lively", "child", "sassy", "dissonant", "Emotive", "electronic", "抒情", "meditative", "Gloomy", "groovy", " film", "adventure, emotion", "ambitious", "Spiritual", "christmas", "reminiscent", "saloon", "vintage", "梦幻", "爱", "fast_decay", "Comedy", "Asian", "侵略性", "Admirative", " communication", "忧郁"]
          ```
          * `Genre Tagging Prompt` guidelines (General instructions and the example prompt: `"inspiring female uplifting pop"')
          
      """)
  String generateMusicPrompt(@UserMessage String diaryContent);
}