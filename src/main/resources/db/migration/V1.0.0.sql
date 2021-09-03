CREATE TABLE `project` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `description` LONGTEXT NULL,
  `type` VARCHAR(2) NOT NULL COMMENT '_t1_ is TYPE-1, _t2_ is TYPE-3, _t3_ is TYPE-3',
  `status` VARCHAR(2) NOT NULL COMMENT '_df_ is DRAFT, _pb_ is PUBLISHED',
  PRIMARY KEY (`id`));
CREATE TABLE `section` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `description` LONGTEXT NULL,
    `project_id` INT NOT NULL,
    PRIMARY KEY (`id`));
CREATE INDEX `fk_section_2_project_id_idx` ON `section`(project_id);
 ALTER TABLE `section`
    ADD CONSTRAINT `fk_section_2_project_id`
    FOREIGN KEY (`project_id`)
    REFERENCES `project` (`id`);