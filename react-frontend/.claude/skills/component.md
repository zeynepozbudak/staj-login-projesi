# Skill: /component

**Amaç:** React, Vite ve Tailwind CSS kullanarak projenin frontend mimarisine uygun, responsive ve temiz bileşenler (component) oluşturmak.

**Nasıl Çağrılır:** `/component [Bileşen Adı] [Bileşenin Ne Yapacağı]`

**Yapay Zeka İçin Geliştirme Kuralları (Context):**
1. **Mimari:** Sadece Functional Component (Arrow function) yapısını kullan.
2. **Stil:** Harici CSS veya SCSS dosyası oluşturma, tüm stillendirmeler için sadece **Tailwind CSS** class'larını kullan. 
3. **Formlar:** Eğer bileşen bir form içeriyorsa, validasyon için kesinlikle `react-hook-form` ve `zod` paketlerini projeye uygun şekilde entegre et.
4. **Veri Yönetimi:** API istekleri için standart `fetch` yerine proje içinde kurulu olan `axios` interceptor yapısını (`import api from '../api'`) kullan.
5. **Kod Temizliği:** Props'ları her zaman destructuring ile al. Gereksiz yorum satırlarından kaçın, sadece karmaşık iş mantığını açıkla.
6. **İhracat:** Bileşeni her zaman dosyanın en altında `export default ComponentAdi;` şeklinde dışa aktar.

**Örnek Kullanım:**
Kullanıcı: `/component UserProfileCard kullanıcının adını ve e-postasını gösteren mavi temalı bir kart yap.`
Yapay Zeka: Yukarıdaki kuralları uygulayarak temiz bir React kodu çıktısı verir.