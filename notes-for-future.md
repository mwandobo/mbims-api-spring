You can create a public class then create a static method inside it, you wont have to instatiate that class to use that method



if (search != null && !search.trim().isEmpty()) {
spec = spec.and((root, query, cb) ->
cb.or(
cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%")
)
);
}