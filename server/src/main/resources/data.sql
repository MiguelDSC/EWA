INSERT INTO `role` (id, name) VALUES (1, 'Hydrologist');
INSERT INTO `role` (id, name) VALUES (2, 'Botanist');
INSERT INTO `role` (id, name) VALUES (3, 'Geologist');
INSERT INTO `role` (id, name) VALUES (4, 'Agronomist');
INSERT INTO `role` (id, name) VALUES (5, 'Climate Scientist');
INSERT INTO `role` (id, name) VALUES (6, 'Guest');

INSERT INTO `team` (id, name) VALUES (1, 'Team Amsterdam');
INSERT INTO `team` (id, name) VALUES (2, 'Team Den Haag');
INSERT INTO `team` (id, name) VALUES (3, 'Team Utrecht');

INSERT INTO `user` (id, email, firstname, image_path, password, surname) VALUES (1, 'bart@hva.nl', 'Bart', 'nan', '$2a$13$wSi2BmwwfxLTlXXm1YKjxu7HEQIwdZ2cOV.Ct.uWkedHwjZ/Sg8ZW', 'Salfischberger');
-- INSERT INTO `user_team` (id, level, role_id, team_id, user_id) VALUES (7, 3, 6, 1, 1);
INSERT INTO `user_team` (id, level, role_id, team_id, user_id) VALUES (1, 1, 5, 1, 1);
INSERT INTO `user_team` (id, level, role_id, team_id, user_id) VALUES (4, 3, 6, 2, 1);
INSERT INTO `user_team` (id, level, role_id, team_id, user_id) VALUES (8, 1, 4, 3, 1);

-- INSERT INTO `user` (id, email, firstname, image_path, password, surname) VALUES (4, 'test', 'Miguel', 'nan', '$2a$12$ybaJVXPWTHrEwJxM4oNjvOQBNtMOSNBT2x/wBVA43mddzX/i6yP5O', 'Cripsem');
-- INSERT INTO `user_team` (id, level, role_id, team_id, user_id) VALUES (5, 1, 1, 1, 4);

INSERT INTO note (id, content, created_date, note_category, shared, title, updated_date, created_by_id)
VALUES (220,'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. ', '2022-01-08 15:35:55.128', 'HYDROLOGY', TRUE, 'Hydrology test results', null, 1);

INSERT INTO note (id, content, created_date, note_category, shared, title,  updated_date, created_by_id)
VALUES (221, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.', '2022-01-08 14:21:22.148', 'HYDROLOGY', TRUE, 'Water Levels Section 2', null, 1);




INSERT INTO `user` (id, email, firstname, image_path, password, surname) VALUES (2, 'edsger@cwi.nl', 'Edsger', 'nan',
                                                                                 '$2a$13$wSi2BmwwfxLTlXXm1YKjxu7HEQIwdZ2cOV.Ct.uWkedHwjZ/Sg8ZW', 'Dijkstra');
INSERT INTO `user_team` (id, level, role_id, team_id, user_id) VALUES (2, 2, 3, 1, 2);

INSERT INTO `user` (id, email, firstname, image_path, password, surname) VALUES (3, 'yahia@hva.nl', 'Yahia', 'nan', '$2a$13$wSi2BmwwfxLTlXXm1YKjxu7HEQIwdZ2cOV.Ct.uWkedHwjZ/Sg8ZW', 'El Sherbini');
INSERT INTO `user_team` (id, level, role_id, team_id, user_id) VALUES (3, 2, 3, 1, 3);

INSERT INTO `greenhouse` (id, team_id) VALUES (1, 1);
INSERT INTO `greenhouse` (id, team_id) VALUES (2, null);
INSERT INTO `greenhouse` (id, team_id) VALUES (3, null);
INSERT INTO `greenhouse` (id, team_id) VALUES (4, null);
INSERT INTO `greenhouse` (id, team_id) VALUES (5, null);
INSERT INTO `greenhouse` (id, team_id) VALUES (6, null);
INSERT INTO `greenhouse_setting` (id, air_humidity, air_temperature, exposure, lighting_hex, soil_humidity,
                                  soil_mix_id, soil_temperature, timestamp, water_mix_id, waterph, greenhouse_id)
                                  VALUES (1, 50, 20, 4, '#ffffff', 50, 1, 15, now(), 4, 5, 1);
INSERT INTO `greenhouse_setting` (id, air_humidity, air_temperature, exposure, lighting_hex, soil_humidity,
                                  soil_mix_id, soil_temperature, timestamp, water_mix_id, waterph, greenhouse_id)
VALUES (2, 50, 20, 4, '#ffffff', 50, 1, 15, now(), 4, 5, 2);
INSERT INTO `greenhouse_setting` (id, air_humidity, air_temperature, exposure, lighting_hex, soil_humidity,
                                  soil_mix_id, soil_temperature, timestamp, water_mix_id, waterph, greenhouse_id)
VALUES (3, 50, 20, 4, '#ffffff', 50, 1, 15, now(), 4, 5, 3);
INSERT INTO `greenhouse_setting` (id, air_humidity, air_temperature, exposure, lighting_hex, soil_humidity,
                                  soil_mix_id, soil_temperature, timestamp, water_mix_id, waterph, greenhouse_id)
VALUES (4, 50, 20, 4, '#ffffff', 50, 1, 15, now(), 4, 5, 4);
INSERT INTO `greenhouse_setting` (id, air_humidity, air_temperature, exposure, lighting_hex, soil_humidity,
                                  soil_mix_id, soil_temperature, timestamp, water_mix_id, waterph, greenhouse_id)
VALUES (5, 50, 20, 4, '#ffffff', 50, 1, 15, now(), 4, 5, 5);
INSERT INTO `greenhouse_setting` (id, air_humidity, air_temperature, exposure, lighting_hex, soil_humidity,
                                  soil_mix_id, soil_temperature, timestamp, water_mix_id, waterph, greenhouse_id)
VALUES (6, 50, 20, 4, '#ffffff', 50, 1, 15, now(), 4, 5, 6);

