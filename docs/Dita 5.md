Dita 5 — Audio, Shop, Maps & Skins
Objektivi: Shtimi i sistemit të audios, dyqanit dhe personalizimit të lojës.

1.AudioManager: Klasa e dedikuar me loadClip(), playMusic(), stopMusic(), playSFX() dhe cleanup() — menaxhon të gjithë audion pa e ndotur klasat e tjera

2.Background Music & SFX: Muzika starton me lojën dhe ndalon me crash — crashSFX.wav dhe click.wav integrohen në të gjitha butonat

3.SaveManager: Shkruan dhe lexon highScore dhe totalMoney nga save.txt — progresi ruhet mes sesioneve

4.Ekonomia: Çdo 10 pikë = 1 monedhë, totalMoney dhe highScore ruhen automatikisht pas çdo game over

5.ShopPanel: Ekran i ri me CardLayout — Maps (Snow, Desert, Default) dhe Skins (Police, Motorcycle, Normal) me sistem çmimesh

6.Maps: drawRoad() ndryshon dinamikisht grassColor, roadColor dhe kerbColor bazuar në MainFrame.currentMap

7.Skins: Police skin me dritat pulsante (alternojnë kuq/blu), Motorcycle skin me hitbox të ngushtë 20px

=====================================================================================================================================
---Bugs për t'u rregulluar nesër---

-Muzika nuk rifillon kur kthehet te Menu pas game over — audioManager.playMusic() duhet të thirret në switchTo(MENU) brenda MainFrame
-moneyLabel në Shop nuk rifresohet kur hyn nga Menu — duhet updateUI() metodë që thirret sa herë shfaqet ShopPanel