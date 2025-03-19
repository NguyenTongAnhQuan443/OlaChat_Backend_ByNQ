/*
 * @ (#) MediaRepository.java    1.0    19/03/2025
 * Copyright (c) 2025 IUH. All rights reserved.
 */
package vn.edu.iuh.fit.repositories;/*
 * @description:
 * @author: Bao Thong
 * @date: 19/03/2025
 * @version: 1.0
 */

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.models.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
