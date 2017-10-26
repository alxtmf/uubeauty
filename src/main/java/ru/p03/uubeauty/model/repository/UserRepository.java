/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty.model.repository;

import java.util.List;
import ru.p03.classifier.model.Classifier;
import ru.p03.uubeauty.model.repository.exceptions.NonexistentEntityException;
import ru.p03.uubeauty.model.ClsUser;

/**
 *
 * @author timofeevan
 */
public interface UserRepository {

	<T extends Classifier> List<T> find();
        
        <T extends Classifier> List<T> find(boolean isDeleted);
        
        <T extends Classifier> List<T> find(int maxResults, int firstResult);
        
        <T extends Classifier> void delete(Long id) throws NonexistentEntityException;
        
        <T extends Classifier> void create(T object);

        <T extends Classifier> void edit(T object) throws NonexistentEntityException, Exception;

}
