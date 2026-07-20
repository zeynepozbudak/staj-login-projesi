import { test, expect } from '@playwright/test';

test('Başarılı giriş senaryosu (E2E)', async ({ page }) => {
  // 1. Robotumuz login sayfasına gidiyor (Vite portun 5173 ise burayı 5173 yap)
  await page.goto('http://localhost:5174/login');

  // 2. Robot, e-posta ve şifre kutularını bulup içine yazıyor
  // Not: Eğer inputlarına özel bir id veya name verdiysen burayı ona göre güncelleyebiliriz.
  // Şimdilik sayfadaki ilk email ve password tipindeki kutuları dolduracak.
  await page.fill('input[type="email"]', 'znpozbudak@gmail.com'); // Buraya veritabanında olan geçerli bir e-posta yaz
  await page.fill('input[type="password"]', '12345678');      // Buraya o e-postanın şifresini yaz

  // 3. Giriş butonuna tıklıyor
  await page.click('button[type="submit"]');

  // 4. En önemli kısım: QA Testinin geçmesi için bir "Kanıt" arıyoruz.
  // Ekranda başarılı giriş yaptığımızda çıkan "Giriş başarılı" yazısını görmeyi bekliyoruz.
  await expect(page.getByText('Giriş başarılı')).toBeVisible();
});