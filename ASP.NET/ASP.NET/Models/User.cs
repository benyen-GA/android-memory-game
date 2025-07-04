namespace ASP.NET.Models;

public class User
{

  public User()
  {
    Id = Guid.NewGuid().ToString();
  }
  private string Id { get; set; }
  private string Username { get; set; }
  private string Password { get; set; }
  private Boolean IsPaidUser { get; set; }
  
  // to establish 1 to 1 relationship w User and Leaderboard once Leaderboard is up
  
}