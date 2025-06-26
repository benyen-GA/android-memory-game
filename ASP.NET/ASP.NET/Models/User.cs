namespace ASP.NET.Models;

public class User
{
  private int id { get; set; }
  private string username { get; set; }
  private string password { get; set; }
  private Boolean isPaidUser { get; set; }
  
  // to establish 1 to 1 relationship w User and Leaderboard
  
}