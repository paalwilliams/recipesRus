package org.launchcode.recipeapp.controllers;

import org.launchcode.recipeapp.models.data.RecipeRepository;
import org.launchcode.recipeapp.models.data.UserRecipeRepository;
import org.launchcode.recipeapp.models.data.UserRepository;
import org.launchcode.recipeapp.models.Recipe;
import org.launchcode.recipeapp.models.User;
import org.launchcode.recipeapp.models.UserRecipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Oksana
 */
@Controller
@RequestMapping("users")
public class UserController {

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private UserRecipeRepository userRecipeRepository;

   @Autowired
   private RecipeRepository recipeRepository;


   @GetMapping()
   public String getAllUsers(Model model) {
      List<User> users = new ArrayList<>();

      Iterable<User> usersIter = userRepository.findAll();


      usersIter.forEach(users::add);


      model.addAttribute("users", users);
      return "users/index";
   }

   @GetMapping("/profile")
   public String getUserProfile(HttpServletRequest request, Model model) {
      User sessionUser = (User) request.getSession().getAttribute("user");
      if (sessionUser == null) {
         model.addAttribute("title", "No user found");
      } else {
         List<Recipe> recipes = new ArrayList<>();
         List<UserRecipe> userRecipes = userRecipeRepository.getAllByUser(sessionUser);

         for (UserRecipe userRecipe : userRecipes) {
            Recipe recipe = userRecipe.getRecipe();
            recipes.add(recipe);
         }

         model.addAttribute("title", sessionUser.getUsername());
         model.addAttribute("user", sessionUser);
         model.addAttribute("recipes", recipes);

      }
      return "users/profile";
   }

   @PostMapping("/addRecipe/{id}")
   public String addRecipe(@PathVariable Integer id, HttpServletRequest request, Model model) {
      User sessionUser = (User) request.getSession().getAttribute("user");

      Optional<Recipe> recipeOptional = recipeRepository.findById(id);
      if (recipeOptional.isPresent()) {
         User user = userRepository.getById(sessionUser.getId());
         Recipe recipe = recipeOptional.get();
         UserRecipe userRecipe = new UserRecipe();
         userRecipe.setUser(user);
         userRecipe.setRecipe(recipe);
         userRecipeRepository.save(userRecipe);


         model.addAttribute("user", userRecipe.getUser());
         model.addAttribute("recipe", userRecipe.getRecipe());
      }


      return "redirect:/users/profile";
   }

}