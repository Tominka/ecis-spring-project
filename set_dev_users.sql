INSERT INTO security.user_settings (id, default_page, default_page_after_login, default_page_after_refresh, id_camp, auto_filter_by_selected_encampment,created_by) VALUES
    (1, '/', true, false, NULL, false, 'SYSTEM'),
    (2, '/', true, false, NULL, false, 'SYSTEM');

INSERT INTO security.user (username,name,surname,email,password,system_admin,password_changed,id_user_settings,enabled,"2fa_enabled",created_by) VALUES
    ('tomas','Muflon','Versmírný','muflon@vesmir.sP','$2y$10$ggEG5FrxU8hUjthW2XtiW./dVTJBi0Nf1aqJfC1HEATo3sqs3/GoG',true,'2024-03-18 22:46:10',1,true,false,'SYSTEM'),
    ('knedlik','Knedlík','Kynutý','knedlik@kralovstvi.cz','$2y$10$U.MJZi4AJUnBtMSOSjF9oO7PXSbDVjyZg.1j2Yt/rckGMFFnvqQ0y',false,'2036-09-14 20:59:33',2,true,false,'SYSTEM');

INSERT INTO ecis.camp (name,location,price,date_from,date_to,enabled,archived_at,created_at,created_by,updated_at,updated_by,version) VALUES
    ('Letní dobrodružství v lese','Šumava - Modrava',4500.0,'2026-07-05','2026-07-12',TRUE,NULL,'2026-02-10 09:15:22','SYSTEM',NULL,NULL,0),
    ('Expedice za pokladem','Rabštejn nad Střelou',5200.0,'2026-07-13','2026-07-20',TRUE,NULL,'2026-02-11 10:30:12','SYSTEM',NULL,NULL,0),
    ('Mladí rytíři a princezny','Zbiroh - Plzeňsko',4800.0,'2026-08-01','2026-08-08',TRUE,NULL,'2026-02-12 14:22:41','SYSTEM',NULL,NULL,0),
    ('Sportovní tábor 2026','Třemošná u Plzně',3900.0,'2026-07-20','2026-07-27',TRUE,NULL,'2026-02-13 08:11:05','SYSTEM',NULL,NULL,0),
    ('Harry Potter: Kouzelnická škola','Nezvěstice',6500.0,'2026-08-10','2026-08-17',TRUE,NULL,'2026-02-14 12:45:33','SYSTEM',NULL,NULL,0),
    ('Robinsonův ostrov','Klatovy - okolí',5700.0,'2026-07-01','2026-07-10',TRUE,NULL,'2026-02-15 16:05:18','SYSTEM',NULL,NULL,0),
    ('Cesta kolem světa za 7 dní','Přeštice',4300.0,'2026-08-18','2026-08-25',TRUE,NULL,'2026-02-16 09:42:10','SYSTEM',NULL,NULL,0),
    ('Detektivní akademie','Manětín',5100.0,'2026-07-27','2026-08-03',TRUE,NULL,'2026-02-17 11:20:55','SYSTEM',NULL,NULL,0),
    ('Záchranáři v akci','Rokycany - Borek',4700.0,'2026-08-03','2026-08-10',TRUE,NULL,'2026-02-18 13:37:29','SYSTEM',NULL,NULL,0),
    ('Tajemství starého hradu','Horšovský Týn',6000.0,'2026-08-15','2026-08-22',TRUE,NULL,'2026-02-19 15:55:44','SYSTEM',NULL,NULL,0),
    ('Zimní výprava na sněhu','Železná Ruda',3500.0,'2026-01-15','2026-01-18',FALSE,'2026-02-01 10:00:00','2026-01-01 08:00:00','SYSTEM','2026-02-01 10:00:00','cron_archive',1),
    ('Levný příměstský tábor','Plzeň - Bolevec',1500.0,'2026-07-06','2026-07-10',TRUE,NULL,'2026-03-01 09:00:00','ADMIN',NULL,NULL,0),
    ('Prémiový tábor s koňmi','Ranč u Tachova',8500.0,'2026-08-24','2026-08-31',TRUE,NULL,'2026-03-05 14:20:00','SYSTEM',NULL,NULL,0);